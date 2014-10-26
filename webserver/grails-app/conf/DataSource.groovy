environments {
    development {
        grails {
            mongo {
                host = "localhost"
                databaseName = "mb_users"
            }
        }
    }
    test {
        grails {
            mongo {
                host = "localhost"
                databaseName = "mb_users"
            }
        }
    }
    production {
        grails {
            mongo {

                // replicaSet = []
                host = "localhost"
                username = ""
                password = ""
                databaseName = "mb_users"
            }
        }
    }
}