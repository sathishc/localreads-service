import com.squareprism.localreads.Ownership
import com.squareprism.localreads.User
import com.squareprism.localreads.Role
import com.squareprism.localreads.UserRole
import com.squareprism.localreads.Book

class BootStrap {

    def init = { servletContext ->

        //create a couple of users to test creation function

        def adminRole = Role.findOrCreateWhere([authority: 'ROLE_ADMIN'])
        adminRole.save(flush: true)

        def userRole = Role.findOrCreateWhere([authority: 'ROLE_USER'])
        userRole.save(flush: true)
    }
    def destroy = {
    }
}
