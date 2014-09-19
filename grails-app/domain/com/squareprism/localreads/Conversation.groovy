package com.squareprism.localreads

class Conversation {

    String id
    User user1
    User user2

    static constraints = {
        user1 blank:false
        user2 blank:false
    }

    static hasMany = [snippets:Snippet]

    static embedded = ['snippets']

}

class Snippet{
    String senderUserId
    String message
    long creationTime

    static constraints = {
        senderUserId nullable: false
        message blank: false, max: 300
        creationTime blank:false
    }

}
