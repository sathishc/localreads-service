package com.squareprism.localreads

import groovyx.net.http.RESTClient
import spock.lang.Specification

import static groovyx.net.http.ContentType.URLENC

/**
 * Created by SatSang on 9/4/14.
 */
class ConversationControllerSpec extends Specification {

    static RESTClient restClient
    static String thisUserId
    static String thisSecondUserId // useful to test search items from other users
    static String thisConversationId


    def setupSpec(){

        restClient = new RESTClient("http://localhost:8080/")
        def postBody = [
                username:"me@foo.com",
                password:"password",
                latitude:"13.0",
                longitude:"75.5"
        ]
        def response = restClient.post(
                path:"register/add",
                requestContentType:URLENC,
                contentType:"application/json",
                body:postBody
        )
        thisUserId = response.data.id

        //create the second user as well
        postBody = [
                username:"me2@foo.com",
                password:"password2",
                latitude:"13.0",
                longitude:"75.5"
        ]

        response = restClient.post(
                path:"register/add",
                requestContentType:URLENC,
                contentType:"application/json",
                body:postBody
        )
        thisSecondUserId = response.data.id


    }

    def cleanupSpec(){
        def deleteUrl = "api/users/" + thisUserId
        def response = restClient.delete(
                path:deleteUrl,
                contentType:"application/json",
                headers:['Authorization':performRestLogin()]
        )

        deleteUrl = "api/users/" + thisSecondUserId
        response = restClient.delete(
                path:deleteUrl,
                contentType:"application/json",
                headers:['Authorization':performRestLoginSecond()]
        )

    }

    def performRestLogin(){
        def postBody = [
                username:"me@foo.com",
                password:"password",
        ]
        def loginResponse = restClient.post(
                path:"api/login",
                requestContentType:"application/json",
                contentType:"application/json",
                body:postBody
        )
        return loginResponse.data.access_token
    }

    def performRestLoginSecond(){
        def postBody = [
                username:"me2@foo.com",
                password:"password2",
        ]
        def loginResponse = restClient.post(
                path:"api/login",
                contentType:"application/json",
                body:postBody
        )
        return loginResponse.data.access_token
    }


    // Create conversations and snippets and then remove them

    def "create Conversations from one user to another" () {
        given:
            def messageText = "Hello, How are you?"
            String createUrl = "http://localhost:8080/api/conversations/create/"
        when:
            def response = restClient.get(
                    path:createUrl,
                    query:[withUserId:thisSecondUserId,messageText:messageText],
                    contentType:"application/json",
                    headers:['Authorization':performRestLogin()]
            )
            println response.data
            thisConversationId = response.data.conversation.id

        then:
            assert response.status == 200
            assert response.data.message == messageText
            assert response.data.conversation.id != null
            assert response.data.conversation.snippets.size() == 1
    }

    def "create snippets from User Two to User One" () {
        given:
        def messageText = "Hello, I'm doing well, How are you?"
        String createUrl = "http://localhost:8080/api/conversations/create/"
        when:
        def response = restClient.get(
                path:createUrl,
                query:[withUserId:thisUserId,messageText:messageText],
                contentType:"application/json",
                headers:['Authorization':performRestLoginSecond()]
        )
        println response.data

        then:
        assert response.status == 200
        assert response.data.message == messageText
        assert response.data.conversation.id == thisConversationId
        assert response.data.conversation.snippets.size() == 2
    }

/*
    def "create many Ownerships from Book Volumes User One" () {
        given:
            String createUrl = "http://localhost:8080/api/ownerships/create/" +  volumeId
            def response = restClient.get(path:createUrl,contentType:"application/json",headers:['Authorization':performRestLogin()])

        expect:
              status == response.status
              dataStatus == response.data.status


        where:
           volumeId     | status   | dataStatus
        "ivzfRJGrdFsC"  | 200      |   true
        "d5xgYw4Ts0gC"  | 200      |   true
    }

    def "create many Ownerships from Book Volumes User Two" () {
        given:
        String createUrl = "http://localhost:8080/api/ownerships/create/" +  volumeId
        def response = restClient.get(path:createUrl,contentType:"application/json",headers:['Authorization':performRestLoginSecond()])

        expect:
        status == response.status
        dataStatus == response.data.status


        where:
        volumeId     | status   | dataStatus
        "KYXcIqhCbkIC"  | 200      |   true
        "NebLAAAACAAJ"  | 200      |   true
    }


    def "add Snippets to this conversation" () {
        given:
            def postBody =[bookId:thisBookId]  // this is a book id in the database

        when:
            def response = restClient.post(
                    path:"api/ownerships",
                    body:postBody,
                    requestContentType:URLENC,
                    contentType:"application/json",
                    headers:['Authorization':performRestLogin()]
            )
            thisOwnershipId = response.data.id

        then:
            assert response.status == 200
            assert response.data.id != null
            assert response.data.status == true
    }



    def "get the added Ownership belonging to this user" () {
        given:

            def showUrl = "api/ownerships/" + thisOwnershipId
        when:
        def response = restClient.get(
                path:showUrl,
                contentType:"application/json",
                headers:['Authorization':performRestLogin()]
        )
        then:
            assert response.status == 200
            assert response.data.status == true
            assert response.data.ownership != null

    }


    def "list Ownerships belonging to user one" () {
        given:
            def listUrl = "api/ownerships/"

        when:
            def response = restClient.get(
                    path:listUrl,
                    contentType:"application/json",
                    headers:['Authorization':performRestLogin()]
            )

        then:
            assert response.status == 200
            assert response.data.status == true
            assert response.data.books.size() == 3

    }

    def "search for Books with a query " () {
        given:

        def searchUrl = "http://localhost:8080/api/ownerships/search/" + "Google Story"
        when:
        def response = restClient.get(
                path:searchUrl,
                contentType:"application/json",
                headers:['Authorization':performRestLogin()]
        )

        then:
        assert response.status == 200
        assert response.data.status == true
        assert response.data.books.size() != 0

    }

    def "search for Books with another query " () {
        given:

        def searchUrl = "http://localhost:8080/api/ownerships/search/" + "Grateful Dead"
        when:
        def response = restClient.get(
                path:searchUrl,
                contentType:"application/json",
                headers:['Authorization':performRestLoginSecond()]
        )

        then:
        assert response.status == 200
        assert response.data.status == true
        assert response.data.books.size() != 0

    }

    def "search for a Book that wont match a query " () {
        given:

        def searchUrl = "http://localhost:8080/api/ownerships/search/" + "Steve Jobs"
        when:
        def response = restClient.get(
                path:searchUrl,
                contentType:"application/json",
                headers:['Authorization':performRestLogin()]
        )

        then:
        assert response.status == 200
        assert response.data.status == false

    }



    def "delete Ownerships belonging to this user" () {
        given:
            def deleteUrl = "http://localhost:8080/api/ownerships/" + thisOwnershipId
        when:
            def response = restClient.delete(
                    path:deleteUrl,
                    contentType:"application/json",
                    headers:['Authorization':performRestLogin()]
            )
        then:
            assert response.status == 200
            assert response.data.status == true

    }


   def "list Ownerships belonging to this user after deletion of 1 ownership" () {
       given:
            def listUrl = "api/ownerships/"

       when:
           def response = restClient.get(
                   path:listUrl,
                   contentType:"application/json",
                   headers:['Authorization':performRestLogin()]
           )

       then:
           assert response.status == 200
           assert response.data.status == true
           assert response.data.books.size() == 2

   }
*/

}
