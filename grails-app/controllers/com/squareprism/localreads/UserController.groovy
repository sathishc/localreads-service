package com.squareprism.localreads

import grails.converters.JSON
import grails.mongodb.geo.Point
import grails.rest.RestfulController

class UserController extends RestfulController {

    def springSecurityService
    static responseFormats = ['json']
    static allowedMethods = ['update','delete','show']

    UserController() {
        super(User)
    }

    def save(){
        String userName = springSecurityService.principal.username
        def postedUser = User.findByUsername(userName)

        def latitude = params.double('latitude')
        def longitude = params.double('longitude')
        def searchRadius = params.long('searchRadius')

        if(latitude == null || longitude == null){
            respond status: false,message: "Invalid Lat/Long"
            return
        }

        if(searchRadius == null){
            respond status: false,message: "Invalid search radius"
            return
        }

        Point geoPoint = new Point(longitude,latitude)
        postedUser.location = geoPoint
        postedUser.settings.searchRadius = searchRadius

        postedUser.save(flush: true,failOnError: true);

        def enrichedUser = [
                id:postedUser.id,
                displayName:postedUser.settings.profileName,
                username:postedUser.username,
                latitude:postedUser.location.y,
                longitude:postedUser.location.x,
                searchRadius:postedUser.settings.searchRadius
        ]


        respond status:true, user:enrichedUser
    }

    def show(){
        String userName = springSecurityService.principal.username
        def thisUser = User.findByUsername(userName)

        def enrichedUser = [
                id:thisUser.id,
                displayName:thisUser.settings.profileName,
                username:thisUser.username,
                latitude:thisUser.location.y,
                longitude:thisUser.location.x,
                searchRadius:thisUser.settings.searchRadius
        ]
        respond user:enrichedUser,status: true
    }

    def delete(User thisUser){
        def response = [status:false,message:'Could not find user in system'];
        if(thisUser == null){
            render response as JSON
            return
        }

        //remove all ownerships created by this user
        def ownerships = Ownership.where{
            user == thisUser
        }.list()

        log.debug("Deleting ownerships")
        ownerships.each {
            it.delete(flush: true)
        }

        //remove all conversations created by this user
        def conversations = Conversation.where{
            user1 == thisUser  ||
            user2 == thisUser
        }.list()

        log.debug("Deleting conversations")
        conversations.each {
            it.delete(flush: true)
        }

        log.debug("Deleting user roles")
        // remove the UserRole where the user is associated
        def userRole = UserRole.findByUser(thisUser)
        userRole.delete(flush: true)


        log.debug("Deleting user object")
        //delete the User object itself
        thisUser.delete(flush: true);
        response = [status:true,message:'Successfully removed user'];
        render response as JSON
    }
}
