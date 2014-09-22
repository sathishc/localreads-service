package com.squareprism.localreads

import grails.converters.JSON
import grails.mongodb.geo.Point

class UserRegistrationController {

    def addUser(){

        String userName =  params.username
        String password =  params.password
        String displayName =  params.displayName
        String imageUrl =  params.imageUrl

        def response = null
        if(userName == null || password == null){
            response = [status:false,message:'Invalid user name or password']
            render response as JSON
            return
        }

        boolean validValue = true


        double latitude =   params.double('latitude')
        double longitude =  params.double('longitude')

        if(latitude == null || longitude == null){
            response = [status:false,message:'Invalid location specified']
            render response as JSON
            return
        }


        if(!validValue){
            response = [status:false,message:'Invalid location specified']
            render response as JSON
            return
        }




        // convert stings to doubles
        //latitude = Double.parseDouble(latitude)
        //longitude = Double.parseDouble(longitude)

        //check if user already present
        def newUser = User.findByUsername(userName)
        if(newUser){
            response = [status:false,message:'User already in system',id:newUser.id]
            render response as JSON
            return
        }

        Settings settings = new Settings(profileName: displayName,userThumbnail: imageUrl)
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

        response = [status:true,message:'User registered. Please login',user:newUser,id:newUser.id]
        render response as JSON
    }

}
