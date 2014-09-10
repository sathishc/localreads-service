package com.squareprism.localreads

import grails.mongodb.geo.Circle
import grails.rest.RestfulController
import grails.transaction.Transactional

class OwnershipController extends RestfulController {

    static responseFormats = ['json']
    static allowedMethods = ['save','index','delete','show','create']

    def springSecurityService
    def bookService


    OwnershipController() {
        super(Ownership)
    }


    // create an ownership from a book id (an existing book in localreads) provided by the user
    @Transactional
    def create(){

        // get the Book that needs to be attached to the ownership
        String volumeId = params.volumeId

        // Check if the volume is already in system, if not create one
        def book = Book.findByIdentifier(volumeId)
        if(!book){
            def bookDetails = bookService.getBookFromVolumeId(volumeId)
            log.info bookDetails

            String title = bookDetails.volumeInfo.title
            String description = bookDetails.volumeInfo.description
            String thumbnail = bookDetails.volumeInfo.imageLinks.thumbnail
            String author = bookDetails.volumeInfo.authors[0]
            book = Book.findOrSaveWhere(
                    identifier: volumeId,
                    name: title,
                    description: description,
                    thumbnail: thumbnail,
                    author: author
            )
        }

        if(!book){
            respond message:'Could not save book in system', status:false
            return
        }


        def userName = springSecurityService.principal.username
        def thisUser = User.findByUsername(userName)
        if(!thisUser){
            respond message:'Could not find the user', status:false
            return
        }


        // Check to see if the ownership is already created and just return it
        // if that's the case, else create a new ownership
        def ownership = Ownership.findOrCreateWhere(book: book,user: thisUser)
        if(ownership.hasErrors()){
            respond message:ownership.errors, status:false
            return
        }

        if(!ownership.save(flush: true)){
            respond message:'Could not save ownership', status:false
            return
        }

        respond(message:'Created ownership', status:true, id:ownership.id,bookId:book.id)
    }

    // save an ownership from a book id (an existing book in localreads) provided by the user
    @Transactional
    def save(){

        // get the Book that needs to be attached to the ownership
        String bookId = request.getJSON().getAt("bookId")
        log.info("Adding Ownership of existing book with id ... " + bookId)

        def book = Book.get(bookId)

        if(!book){
            respond message:'Could not find book in system', status:false
            return
        }


        def userName = springSecurityService.principal.username
        def thisUser = User.findByUsername(userName)
        if(!thisUser){
            respond message:'Could not find the user', status:false
            return
        }

        // Check to see if the ownership is already created and just return it
        // if that's the case, else create a new ownership
        def ownership = Ownership.findOrCreateWhere(book: book,user: thisUser)
        if(ownership.hasErrors()){
            respond message:ownership.errors, status:false
            return
        }

        if(!ownership.save(flush: true)){
            respond message:'Could not save ownership', status:false
            return
        }

        respond message:'Saved ownership', status:true, id:ownership.id
    }

    // method to search for books similar to query within a search radius
    def search(){
        String searchQuery = params.query

        def userName = springSecurityService.principal.username
        def thisUser = User.findByUsername(userName)
        if(!thisUser){
            respond message:'Could not find the user', status:false
            return
        }

        // get the search settings for this user
        long radius = thisUser.settings.searchRadius //km
        double latitude = thisUser.location.y
        double longitude = thisUser.location.x

        //perform a search where the ownership-book text has the search-query and
        // the user is within radius km of the current user
        def books = Book.search(searchQuery)
        if(books == null || books?.size()==0){
            respond message:'Did not find books with matching criteria', status:false
            return
        }

        def users = User.findAllByLocationGeoWithin(Circle.valueOf([[longitude,latitude],radius]))
        if(users == null || users?.size()==0){
            respond message:'Did not find users nearby with matching criteria', status:false
            return
        }

        log.info books
        log.info users

        def ownerships = Ownership.where{
            book in books ||
            user in users
        }.list()

        respond ownerships:ownerships,status: true
    }

    // returns the ownership for the id specified. the ownership has to belong to the user
    def show(Ownership ownership){
        def userName = springSecurityService.principal.username
        def thisUser = User.findByUsername(userName)
        if(!thisUser){
            respond message:'Could not find the user', status:false
            return
        }

        // check if user owns this ownership
        if(ownership.user != thisUser){
            respond message:'Does not belong to this user', status:false
            return
        }


        respond ownership:ownership, status: true
    }

    // returns the full list of ownerships belonging to this user
    def index(Integer max){

        params.max = Math.min(max ?: 10, 100)

        def userName = springSecurityService.principal.username
        def thisUser = User.findByUsername(userName)
        if(!thisUser){
            respond message:'Could not find the user', status:false
            return
        }

        def ownerships = Ownership.findAllByUser(thisUser)
        respond ownerships:ownerships, status:true
    }

    //delete ownership for this user
    def delete(Ownership ownership){
        def userName = springSecurityService.principal.username
        def thisUser = User.findByUsername(userName)
        if(!thisUser){
            respond message:'Could not find the user', status:false
            return
        }

        // check if user owns this ownership
        if(ownership.user != thisUser){
            respond message:'Does not belong to this user', status:false
            return
        }

        ownership.delete(flush: true)
        respond message:'Deleted ownership', status:true
    }

}
