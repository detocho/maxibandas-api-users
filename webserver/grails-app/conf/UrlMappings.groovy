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

        "/getIp"{
            controller = "User"
            action  =[GET: 'getIp', POST: 'notAllowed', PUT: 'notAllowed', DELETE: 'notAllowed']
        }
    }
}
