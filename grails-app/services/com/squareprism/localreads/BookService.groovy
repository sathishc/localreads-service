package com.squareprism.localreads

import grails.converters.JSON
import grails.transaction.Transactional
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient

@Transactional
class BookService {

    static final googleBookApiUrl = "https://www.googleapis.com/"

    def getBookFromVolumeId(String volumeId) {
        RESTClient restClient = new RESTClient(googleBookApiUrl)
        // rest url for retrieving volume

        String restUrlPath = "books/v1/volumes/" + volumeId
        def response = [error:true,message:"Could not find volume"]
        try{
            response = restClient.get(
                    path:restUrlPath,
                    query:[projection:'lite'],
                    contentType:"application/json",
            )
        }catch (HttpResponseException exception){
            return response
        }

        return response.data
    }
}
