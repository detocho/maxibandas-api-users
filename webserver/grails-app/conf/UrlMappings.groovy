class UrlMappings {

    static mappings = {


        "/$userId?" {
            controller = "User"
            action = [GET: 'getUser', POST: 'addUser', PUT: 'putUser', DELETE: 'notAllowed']
        }



        "/search" {
            controller = "User"
            action = [GET: 'searchUser', POST: 'notAllowed', PUT: 'notAllowed', DELETE: 'notAllowed']
        }
    }
}
