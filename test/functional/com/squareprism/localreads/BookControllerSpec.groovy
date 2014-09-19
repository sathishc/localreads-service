package com.squareprism.localreads

import groovyx.net.http.RESTClient
import spock.lang.Specification
import static groovyx.net.http.ContentType.URLENC

/**
 * Created by SatSang on 9/4/14.
 */
class BookControllerSpec extends Specification {

    static RESTClient restClient
    static String thisUserId

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
    }

    def cleanupSpec(){
        def deleteUrl = "api/users/" + thisUserId
        def response = restClient.delete(
                path:deleteUrl,
                contentType:"application/json",
                headers:['Authorization':performRestLogin()]
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

    // Not required since a book is never added directly
    /*def "Add a book" () {
        given:
           def postBody = [
                   name: "The Da Vinci Code",
                   description:"The Da Vinci Code is a 2003 mystery-detective novel written by Dan Brown. It follows symbologist Robert Langdon and cryptologist Sophie Neveu after a murder in the Louvre Museum in Paris, when they become involved in a battle between the Priory of Sion and Opus Dei over the possibility of Jesus having been married to Mary Magdalene.",
                   identifier:"1234567B",
                   author:"Dan Brown"
           ]

        when:
            def response = restClient.post(
                    path:"api/books",
                    body:postBody,
                    contentType:"application/json",
                    headers:['Authorization':performRestLogin()]
            )
        then:
            assert response.status == 200
            assert response.data.id != null
            assert response.data.identifier.equals("1234567B")
    }*/



    def "Retrieve all books" () {
        given:
            def listBooksUrl = "/api/books"
        when:
            def response = restClient.get(
                    path:listBooksUrl,
                    contentType:"application/json",
                    headers:['Authorization':performRestLogin()]
            )
        then:
            assert response.status == 200
    }

}
