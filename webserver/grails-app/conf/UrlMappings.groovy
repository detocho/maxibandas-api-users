class UrlMappings {

    static mappings = {


        "/$userId?" {
            controller = "User"
            action = [GET: 'getUser', POST: 'addUser', PUT: 'putUser', DELETE: 'notAllowed']
        }



        "/search" {
            controller = "User"
            action = [GET: 'notAllowed', POST: 'searchUser', PUT: 'notAllowed', DELETE: 'notAllowed']
        }

        "/searchByEmail/$email?"{
            controller = "User"
            action  =[GET: 'getUserByEmail', POST: 'notAllowed', PUT: 'notAllowed', DELETE: 'notAllowed']
        }
    }
}
