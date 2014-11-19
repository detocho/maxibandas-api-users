package users

import org.bouncycastle.util.IPAddress

import javax.servlet.http.HttpServletResponse
import grails.converters.*
//import grails.transaction.*
import static org.springframework.http.HttpStatus.*
import static org.springframework.http.HttpMethod.*
import grails.plugin.gson.converters.GSON
import users.exceptions.NotFoundException
import users.exceptions.ConflictException
import users.exceptions.BadRequestException

class UserController {

    def userService

    def notAllowed(){
        def method = request.method

        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED)

        setHeaders()

        def mapResult = [
                message: "Method $method not allowed",
                status: HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                error:"not_allowed"
        ]
        render mapResult as GSON
    }

    def getUserByEmail(){

        def result
        setHeaders()
        try{

            result = userService.searchUserByEmail(params)
            response.setStatus( HttpServletResponse.SC_OK)
            render result as GSON

        }catch (BadRequestException e) {

            renderException(e)

        }catch (ConflictException e){

            renderException(e)

        }catch (Exception e){

            renderException(e)

        }

    }

    def getUser() {

        def result

        setHeaders()


        try{


            result = userService.getUser(params)
            response.setStatus( HttpServletResponse.SC_OK)
            render result as GSON

        }catch(NotFoundException e){

            renderException(e)

        }catch(Exception e){

            renderException(e)
        }


    }

    def addUser(){

        def result
        setHeaders()
        try{

            result = userService.createUser(request.JSON)
            response.setStatus( HttpServletResponse.SC_CREATED)
            render result as GSON

        }catch (BadRequestException e) {

            renderException(e)

        }catch (ConflictException e){

            renderException(e)

        }catch (Exception e){

            renderException(e)

        }




    }

    def putUser(){

        setHeaders()
        try {
            def modifiedUser = userService.modifyUser(params, request.JSON)
            response.setStatus(HttpServletResponse.SC_OK)
            render modifiedUser as GSON
        }catch(NotFoundException e){

            renderException(e)

        }catch (BadRequestException e) {

            renderException(e)

        }catch (Exception e){

            renderException(e)

        }
    }

    def searchUser(){

        setHeaders()

        try{

            def result = userService.searchUser(params, request.JSON)
            response.setStatus(HttpServletResponse.SC_CREATED)
            render result as GSON

        }catch (BadRequestException e){

            renderException(e)

        }catch(NotFoundException e){

            renderException(e)

        }catch (Exception e){

            renderException(e)
        }
    }


    def setHeaders(){

        response.setContentType "application/json; charset=utf-8"
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE");
        response.setHeader("Access-Control-Max-Age", "86400");
        response.setHeader("Access-Control-Allow-Headers", "application/json;charset=UTF-8");
    }

    def renderException(def e){

        def statusCode
        def error

        try{
            statusCode  = e.status
            error       = e.error

        }catch(Exception ex){

            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            error = "internal_server_error"
        }

        response.setStatus(statusCode)

        def mapExcepction = [
                message: e.getMessage(),
                status: statusCode,
                error: error
        ]

        render mapExcepction as GSON

    }
}
