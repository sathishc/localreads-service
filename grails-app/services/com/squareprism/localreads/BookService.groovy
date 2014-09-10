package com.squareprism.localreads

import grails.converters.JSON
import grails.transaction.Transactional
import groovyx.net.http.RESTClient

@Transactional
class BookService {

    static final googleBookApiUrl = "https://www.googleapis.com/"

    def getBookFromVolumeId(String volumeId) {
        RESTClient restClient = new RESTClient(googleBookApiUrl)
        // rest url for retrieving volume
        String restUrlPath = "books/v1/volumes/" + volumeId
        def response = restClient.get(
                path:restUrlPath,
                query:[projection:'lite',key:'AIzaSyCOiDwUMxx03pQT_mGcskR2mLNJGNbQ4jg'],
                contentType:"application/json",
        )
        return response.data
    }
}
