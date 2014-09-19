package com.squareprism.localreads

class Ownership {

    String id
    Date dateCreated
    Book book
    User user

    static constraints = {
        book blank:false, unique: 'user' //don't allow the same book for this user
        user blank:false
    }

    static mapping = {
        book lazy: false
    }
}
