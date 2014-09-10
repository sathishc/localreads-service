package com.squareprism.localreads

import grails.converters.JSON
import grails.mongodb.geo.Point

class UserRegistrationController {

    def addUser(){

        String userName =  request.getJSON().getAt('username')
        String password =  request.getJSON().getAt('password')
        double latitude =  Double.parseDouble((String)request.getJSON().getAt('latitude'))
        double longitude =  Double.parseDouble((String)request.getJSON().getAt('longitude'))


        def response = null

        if(userName == null){
            response = [status:false,message:'Invalid or null user name']
            render response as JSON
            return
        }

        //check if user already present
        def newUser = User.findByUsername(userName)
        if(newUser){
            response = [status:false,message:'User already in system',id:newUser.id]
            render response as JSON
            return
        }

        Settings settings = new Settings()
        // geoJson points are in long/lat order
        Point location = new Point(longitude,latitude)
        newUser = new User(username: userName,password: password,settings: settings,location: location)

        if(!newUser.validate()){
            response = [status:false,message:'Invalid user name or password',errors:newUser.errors]
            render response as JSON
            return
        }


        if(newUser.save(flush: true) == false){
            response = [status:false,message:'Could not save user',errors: newUser.errors];
            render response as JSON
            return
        }

        def role = Role.findWhere([authority: 'ROLE_USER'])
        def userRole = new UserRole(user: newUser,role: role).save(flush: true)
        if(!(newUser && userRole)){
            response = [status:false,message:'Could not create user role'];
            render response as JSON
            return
        }

        respond status:true,message:'Successfully saved user',user:newUser,id:newUser.id
    }

}
