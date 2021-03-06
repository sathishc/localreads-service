package com.squareprism.localreads

import grails.rest.RestfulController
import grails.transaction.Transactional

class ConversationController extends RestfulController {

    static responseFormats = ['json']
    static allowedMethods = ['save','index','delete','show','create']


    def springSecurityService
    def brokerMessagingTemplate

    ConversationController() {
        super(Conversation)
    }

    def index(){ //get this users conversations

        def userName = springSecurityService.principal.username
        def thisUser = User.findByUsername(userName)

        def conversations = Conversation.where{
            user1 == thisUser || user2 == thisUser
        }.list()

        if(!conversations || conversations?.size() == 0){
            respond status:false, message:"Could not find any conversations"
            return
        }

        def enrichedConversations = conversations.collect{
            def receiverName
            if(it.user1 == thisUser){
                receiverName = it.user2.settings.profileName
            }else{
                receiverName = it.user1.settings.profileName
            }

            [id:it.id,snippets:it.snippets,receiverName:receiverName]
        }

        respond conversations:enrichedConversations, status: true
    }

    @Transactional
    def create(){ //create a new conversation with a user

        log.info "Called conversation create with " + params

        // get the user with whom you want to start a conversation
        def withUser = User.get(params.withUserId)

        if(!withUser){
            respond status:false, message:"Could not find user in system"
            return
        }

        //get the message text
        String messageText = params.messageText
        if(messageText == null || messageText?.size() == 0){
            respond status:false, message:"No message specified"
            return
        }

        def userName = springSecurityService.principal.username
        def thisUser = User.findByUsername(userName)

        // check if a conversation already exists
        def conversation = Conversation.where {
            (user1 == withUser && user2 == thisUser) ||
            (user2 == withUser && user1 == thisUser)
        }.find()

        log.info "Did not find a conversation"

        // if not found create a conversation
        if(!conversation){
            conversation = new Conversation(user1: thisUser,user2: withUser, snippets: [])
            conversation.save(flush: true,failOnError: true)
        }

        //if not saved return
        if(!conversation){
            respond status:false, message:"Could not create conversation"
            return
        }

        log.info "Created or Found a conversation"

        //else add the message to the conversation
        def snippet = new Snippet(senderUserId: thisUser.id,message: messageText,creationTime: System.currentTimeMillis())
        conversation.snippets.add(snippet)
        conversation.save(flush: true, failOnError: true)

        log.info "Added snippet and saved conversation"

        if(conversation.hasErrors()){
            respond status:false, message:"Could not add messages"
            return
        }

        def senderName,receiverName,receiverId
        if(conversation.user1 == thisUser){
            receiverId = conversation.user2.username
            senderName = conversation.user1.settings.profileName
            receiverName = conversation.user2.settings.profileName
        }else{
            receiverId = conversation.user1.username
            senderName = conversation.user2.settings.profileName
            receiverName = conversation.user1.settings.profileName
        }

        // get the users current token
        def token = AuthenticationToken.findByUsername(receiverId).tokenValue
        def conversationSnippet = [
                id:conversation.id,
                snippet:[
                        senderUserId: snippet.senderUserId,
                        creationTime: snippet.creationTime,
                        message: snippet.message
                ],
                receiverName: senderName
        ]
        brokerMessagingTemplate.convertAndSend("/queue/conversations/" + token,conversationSnippet)

        // respond to the original post
        def enrichedConversation = [id:conversation.id,snippets:conversation.snippets,receiverName:receiverName]
        respond status:true, message:messageText, conversation:enrichedConversation
    }

    @Transactional
    def save(Conversation conversation){ // add a new message to an existing conversation
        // conversation is already available
        if(!conversation){
            respond status:false, message:"Could not find this conversation"
            return
        }

        //get the message text
        String messageText = params.message
        if(messageText == null || messageText?.size() == 0){
            respond status:false, message:"No message specified"
            return
        }

        //get this user
        def userName = springSecurityService.principal.username
        def thisUser = User.findByUsername(userName)

        //else add the message to the conversation
        def snippet = new Snippet(senderUserId: thisUser.id,message: messageText,creationTime: System.currentTimeMillis())
        conversation.snippets.add(snippet)
        conversation.save(flush: true)

        if(conversation.hasErrors()){
            respond status:false, message:"Could not add messages"
            return
        }

        def receiverName,receiverId
        if(conversation.user1 == thisUser){
            receiverId = conversation.user2.username
            receiverName = conversation.user2.settings.profileName
        }else{
            receiverId = conversation.user1.username
            receiverName = conversation.user1.settings.profileName
        }

        // get the users current token
        def token = AuthenticationToken.findByUsername(receiverId).tokenValue
        def conversationSnippet = [
                id:conversation.id,
                snippet:[
                        senderUserId: snippet.senderUserId,
                        creationTime: snippet.creationTime,
                        message: snippet.message
                ],
                receiverName: receiverName
        ]
        brokerMessagingTemplate.convertAndSend("/queue/conversations/" + token,conversationSnippet)

        def enrichedConversation = [id:conversation.id,snippets:conversation.snippets,receiverName:receiverName]
        respond status:true, message:messageText, conversation:enrichedConversation
    }

    // returns the ownership for the id specified. the ownership has to belong to the user
    def show(Conversation conversation){
        def userName = springSecurityService.principal.username
        def thisUser = User.findByUsername(userName)
        if(!thisUser){
            respond message:'Could not find the user', status:false
            return
        }

        // check if user owns this ownership
        if(conversation.user1 != thisUser || conversation.user2 != thisUser){
            respond message:'Does not belong to this user', status:false
            return
        }

        def receiverName
        if(conversation.user1 == thisUser){
            receiverName = conversation.user2.settings.profileName
        }else{
            receiverName = conversation.user1.settings.profileName
        }

        def enrichedConversation = [id:conversation.id,snippets:conversation.snippets,receiverName:receiverName]


        respond conversation:enrichedConversation, status: true
    }

    //delete conversation for this user
    @Transactional
    def delete(Conversation conversation){
        def userName = springSecurityService.principal.username
        def thisUser = User.findByUsername(userName)
        if(!thisUser){
            respond message:'Could not find the user', status:false
            return
        }

        if(!conversation){
            respond message:'Could not find the conversation', status:false
            return
        }

        // check if user owns this ownership
        if(conversation.user1 != thisUser || conversation.user2 != thisUser){
            respond message:'Does not belong to this user', status:false
            return
        }

        def conversationId = conversation.id;
        conversation.delete(flush: true)
        respond message:'Deleted ownership', status:true, id:conversationId
    }

}
