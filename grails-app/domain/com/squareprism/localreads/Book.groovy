package com.squareprism.localreads

class Book {
    String id
    String name
    String description
    String thumbnail
    String identifier
    String genre = "none"
    String review = ""
    int rating = 0
    String author
    String webPageLink
    String isbn


    static constraints = {
        name(blank:false)
        description(nullable:true)
        thumbnail(nullable: true)
        identifier(nullable: false,unique: true)
        genre(nullable: true)
        review(nullable: true)
        rating(nullable: true)

        author(nullable: true)
        webPageLink(nullable: true)
        isbn(nullable: true)
    }

    static mapping = {
        index description:"text"
    }
}
