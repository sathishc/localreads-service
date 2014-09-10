// Place your Spring DSL code here
import com.squareprism.localreads.User
import grails.rest.render.json.JsonCollectionRenderer
import grails.rest.render.json.JsonRenderer

beans = {
    userRenderer(JsonRenderer, User) {
        excludes = ['password','accountExpired','accountLocked','passwordExpired','class']
    }

    userCollectionRenderer(JsonCollectionRenderer, User) {
        excludes = ['password','accountExpired','accountLocked','passwordExpired','class']
    }
}
