class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/api/ownerships/create/$volumeId"   (controller: "ownership", action: 'create') // create ownership
        "/api/ownerships/search/$query"      (controller: 'ownership', action: 'search') //search mapping
        "/register/add"    (controller: "userRegistration", action: 'addUser') // register a user

        //resource mappings for the domain classes these map CRUD operations

        "/api/users"        (resources: "user")
        "/api/books"        (resources: "book")
        "/api/ownerships"   (resources: "ownership")




        "/"(view:"/index")
        "500"(view:'/error')
	}
}
