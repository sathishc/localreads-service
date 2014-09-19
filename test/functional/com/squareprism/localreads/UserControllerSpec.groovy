package com.squareprism.localreads
import groovyx.net.http.RESTClient
import spock.lang.Specification

import static groovyx.net.http.ContentType.URLENC


/**
 * Created by SatSang on 9/4/14.
 */
class UserControllerSpec extends Specification {

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


    def "test users with login " () {
        given:

        def showUrl = "api/users/" + thisUserId
        when:
        def response = restClient.get(
                path:showUrl,
                contentType:"application/json",
                headers:['Authorization':performRestLogin()]
        )
        then:
            assert response.status == 200
    }



}
