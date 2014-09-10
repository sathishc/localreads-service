import com.squareprism.localreads.Ownership
import com.squareprism.localreads.User
import com.squareprism.localreads.Role
import com.squareprism.localreads.UserRole
import com.squareprism.localreads.Book

class BootStrap {

    def init = { servletContext ->

        //create a couple of users to test creation function

        def adminRole = Role.findOrCreateWhere([authority: 'ROLE_ADMIN'])//new Role(authority: 'ROLE_ADMIN').save(flush: true) // Role.get(1)
        adminRole.save(flush: true)

        def userRole = Role.findOrCreateWhere([authority: 'ROLE_USER'])//new Role(authority: 'ROLE_USER').save(flush: true) // Role.get(2);
        userRole.save(flush: true)

        /*def adminUser = User.findOrCreateWhere([username: 'admin'])
        adminUser.password = 'password'
        adminUser.location = [['lat':13.0d,'long':78.3d]]
        adminUser.save(flush: true,failOnError: true)

        def testUser = User.findOrCreateWhere([username: 'me'])
        testUser.password = 'password'
        testUser.location = [['lat':41.0d,'long':73.5d]]
        testUser.save(flush: true, failOnError: true)

        if(UserRole.findByUserAndRole(adminUser,adminRole) == null){
            UserRole.create adminUser, adminRole, true
        }

        if(UserRole.findByUserAndRole(testUser,userRole) == null){
            UserRole.create testUser, userRole, true
        }

        def book = Book.findOrCreateWhere([
                name: "Da Vinci Code",
                description: "The Da Vinci Code is a 2003 mystery-detective novel written by Dan Brown.",
                identifier: "H124hjyetd98SF",
                author: "Dan Brown"
        ])
        book.save(flush: true,failOnError: true)

        //test user has one book
        def ownership = Ownership.findOrCreateWhere(book: book,user: testUser)
        ownership.save(flush: true,failOnError: true)

        // admin user owns the same book
        ownership = Ownership.findOrCreateWhere(book: book,user: adminUser)
        ownership.save(flush: true)

        book = Book.findOrCreateWhere([
                name: "Angels and Demonss",
                description: "Angels & Demons is a 2000 bestselling mystery-thriller novel written by American author Dan Brown and published by Pocket Books. The novel introduces the character Robert Langdon, who is also the protagonist of Brown's subsequent 2003 novel, The Da Vinci Code; his 2009 novel, The Lost Symbol; and the 2013 novel Inferno. ",
                identifier: "H124hjyetd98SC",
                author: "Dan Brown"
        ])
        book.save(flush: true)

        //admin user has one more
        ownership = Ownership.findOrCreateWhere(book: book,user: adminUser)
        ownership.save(flush: true,failOnError: true)


        assert User.count() == 2
        assert Role.count() == 2
        assert UserRole.count() == 2
        assert Book.count() == 2
        assert Ownership.count() == 3*/




    }
    def destroy = {
    }
}
