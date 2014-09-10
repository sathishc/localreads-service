package com.squareprism.localreads

import grails.converters.JSON
import grails.rest.RestfulController

class UserController extends RestfulController {

    def springSecurityService
    static responseFormats = ['json']
    static allowedMethods = ['update','delete','show']

    UserController() {
        super(User)
    }

    def delete(User thisUser){
        def response = [status:false,message:'Could not find user in system'];
        if(thisUser == null){
            render response as JSON
            return
        }

        //remove all ownerships created by this user
        def ownerships = Ownership.where{
            user == thisUser
        }.list()

        log.debug("Deleting ownerships")
        ownerships.each {
            it.delete(flush: true)
        }

        log.debug("Deleting user roles")
        // remove the UserRole where the user is associated
        def userRole = UserRole.findByUser(thisUser)
        userRole.delete(flush: true)


        log.debug("Deleting user object")
        //delete the User object itself
        thisUser.delete(flush: true);
        response = [status:true,message:'Successfully removed user'];
        render response as JSON
    }
}
