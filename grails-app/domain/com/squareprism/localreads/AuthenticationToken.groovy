package com.squareprism.localreads

class AuthenticationToken {
    String id
    Date dateCreated
    String tokenValue
    String username

    static constraints = {
    }

    def beforeInsert() {
        def existingToken = AuthenticationToken.findByUsername(username)
        if (existingToken) {
            existingToken.delete();
        }
    }
}
