
environments{
    development{
        grails {
            mongo {
                host = "localhost"
                port = 27017
                username = "localreads_admin"
                password = "password"
                databaseName = "localreads_dev"
            }
        }
    }
    test{
        grails {
            mongo {
                host = "localhost"
                port = 27017
                username = "localreads_admin"
                password = "password"
                databaseName = "localreads_test"
            }
        }
    }
    prod{
        grails {
            mongo {
                host = "localhost"
                port = 27017
                username = "localreads_admin"
                password = "password"
                databaseName = "localreads"
            }
        }
    }
}
