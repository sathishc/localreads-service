package com.squareprism.localreads

import grails.rest.RestfulController
import grails.transaction.Transactional

class BookController extends RestfulController {

    static responseFormats = ['json']
    static allowedMethods = ['save']

    BookController() {
        super(Book)
    }

    @Transactional
    def save(Book book){

        log.info("Trying to save book")
        log.info book

        // return true if book already available in system
        def existingBook = Book.findByIdentifier(book.identifier)
        if(existingBook){
            log.info "Book already in system"
            respond message:"Book already in system",status:true, identifier: existingBook.identifier,id:existingBook.id
            return
        }

        if(book.hasErrors()){
            log.info "Failed validation"
            respond message:book.errors,status:false
            return
        }

        if(!book.save(flush: true)){
            log.info "Failed to save"
            respond message:"Could not save Book",status:false
            return
        }

        respond message:"Saved Book",status:true,id:book.id,identifier: book.identifier

    }
}
