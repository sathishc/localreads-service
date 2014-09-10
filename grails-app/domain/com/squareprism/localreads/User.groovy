package com.squareprism.localreads

import grails.mongodb.geo.Point

class User {

	transient springSecurityService

	String username
	String password
	boolean enabled = true
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired

    //Add a String id so that MongoDB provides UUID based ids
    String id
    //Location of user as latitude and longitude
    Point location
    // settings for this user
    Settings settings

	static transients = ['springSecurityService']

	static constraints = {
		username blank: false, unique: true, email: true
		password blank: false
        location nullable: false
        settings nullable: true
	}

	static mapping = {
		password column: '`password`'
        location geoIndex:'2dsphere'
	}

    static embedded = ['settings']


	Set<Role> getAuthorities() {
		UserRole.findAllByUser(this).collect { it.role }
	}

	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}

	protected void encodePassword() {
		password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
	}
}


class Settings {

    long searchRadius = 5
    String privacy = 'all_allowed'
    String accountType
    String profileName
    String userThumbnail

    static constraints = {
        accountType nullable: true
        profileName nullable: true
        userThumbnail nullable: true
    }

}
