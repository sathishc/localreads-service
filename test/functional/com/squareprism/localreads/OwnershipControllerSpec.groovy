package com.squareprism.localreads

import groovyx.net.http.RESTClient
import spock.lang.Specification

/**
 * Created by SatSang on 9/4/14.
 */
class OwnershipControllerSpec extends Specification {

    static RESTClient restClient
    static String thisUserId
    static String thisOwnershipId
    static String thisBookId

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
                contentType:"application/json",
                body:postBody
        )

        println response.data.message
        thisUserId = response.data.id


    }

    def cleanupSpec(){
        def deleteUrl = "api/users/" + thisUserId
        def response = restClient.delete(
                path:deleteUrl,
                contentType:"application/json",
                headers:['X-Auth-Token':performRestLogin()]
        )

    }

    def performRestLogin(){
        def postBody = [
                username:"me@foo.com",
                password:"password",
        ]
        def loginResponse = restClient.post(
                path:"api/login",
                contentType:"application/json",
                body:postBody
        )
        return loginResponse.data.access_token
    }


    def "create Ownerships from a Book Volume" () {
        given:
            String createUrl = "http://localhost:8080/api/ownerships/create/" +  "zyTCAlFPjgYC"
        when:
            def response = restClient.get(
                    path:createUrl,
                    contentType:"application/json",
                    headers:['X-Auth-Token':performRestLogin()]
            )
            thisOwnershipId = response.data.id
            thisBookId = response.data.bookId
            println response.data
        then:
            assert response.status == 200
            assert response.data.id != null
            assert response.data.status == true
    }


    def "add Ownerships to this user" () {
        given:
            def postBody =[bookId:thisBookId]  // this is a book id in the database

        when:
            def response = restClient.post(
                    path:"api/ownerships",
                    body:postBody,
                    contentType:"application/json",
                    headers:['X-Auth-Token':performRestLogin()]
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
                headers:['X-Auth-Token':performRestLogin()]
        )
        then:
            assert response.status == 200
            assert response.data.status == true
            assert response.data.ownership != null

    }


    def "list Ownerships belonging to this user" () {
        given:
            def listUrl = "api/ownerships/"

        when:
            def response = restClient.get(
                    path:listUrl,
                    contentType:"application/json",
                    headers:['X-Auth-Token':performRestLogin()]
            )

        then:
            assert response.status == 200
            assert response.data.status == true
            assert response.data.ownerships.size() == 1

    }

    def "search for Books with a query " () {
        given:

        def searchUrl = "http://localhost:8080/api/ownerships/search/" + "Google Story"
        when:
        def response = restClient.get(
                path:searchUrl,
                contentType:"application/json",
                headers:['X-Auth-Token':performRestLogin()]
        )

        then:
        assert response.status == 200
        assert response.data.status == true
        assert response.data.ownerships.size() == 1

    }

    def "search for Books with another query " () {
        given:

        def searchUrl = "http://localhost:8080/api/ownerships/search/" + "Grateful Dead"
        when:
        def response = restClient.get(
                path:searchUrl,
                contentType:"application/json",
                headers:['X-Auth-Token':performRestLogin()]
        )

        then:
        assert response.status == 200
        assert response.data.status == true
        assert response.data.ownerships.size() == 1

    }

    def "search for a Book that wont match a query " () {
        given:

        def searchUrl = "http://localhost:8080/api/ownerships/search/" + "Steve Jobs"
        when:
        def response = restClient.get(
                path:searchUrl,
                contentType:"application/json",
                headers:['X-Auth-Token':performRestLogin()]
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
                    headers:['X-Auth-Token':performRestLogin()]
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
                   headers:['X-Auth-Token':performRestLogin()]
           )

       then:
           assert response.status == 200
           assert response.data.status == true
           assert response.data.ownerships.size() == 0

   }



}
