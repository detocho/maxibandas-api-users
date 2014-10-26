package users

import java.text.MessageFormat
import org.apache.ivy.plugins.conflict.ConflictManager
import grails.converters.*
import users.exceptions.NotFoundException
import users.exceptions.ConflictException
import users.exceptions.BadRequestException

class UserService {

    static transactional = 'mongo'



    def getUser(def userId){

        Map jsonResult=[:]

        if (!userId) {
            throw new NotFoundException("You must provider user_id")
        }

        def userResult = User.findById(userId)

        if (!userResult){
            throw new NotFoundException("The user with user_id = "+userId+" not found")
        }

        jsonResult = getResult(userResult)

        jsonResult


    }


    def createUser(def jsonUser){

        Map jsonResult=[:]
        def responseMessage = ''

        User newUser = new User(

                phone:      jsonUser?.phone,
                email:      jsonUser?.email,
                password:   jsonUser?.password,
                locationId: jsonUser?.location_id,
                origin:     jsonUser?.origin
        )

        if(!newUser.validate()) {
            newUser.errors.allErrors.each {
                responseMessage += MessageFormat.format(it.defaultMessage, it.arguments) + " "
            }
            throw new BadRequestException(responseMessage)

        }
        newUser.save()

        jsonResult = getResult(newUser)

        jsonResult

    }

    def modifyUser(def userId, def jsonUser){

        Map jsonResult = [:]
        def responseMessage = ''

        if (!userId) {
            throw new NotFoundException("You most provider userid")
        }

        def obteinedUser = User.findById(userId)

        if (!obteinedUser){
            throw new NotFoundException("The User with userId="+userId+" not found")
        }

        //TODO debemos agregar un validaro de json

        //if (user_type == "admin_mp") {
        //obteinedUser.dealerId = jsonUser?.dealer_id
        //obteinedUser.email = jsonUser?.email
        //obteinedUser.origin = jsonUser?.origin
        //obteinedUser.status = jsonUser?.status
        //obteinedUser.userType = jsonUser?.user_type
        //}

        obteinedUser.name           = jsonUser?.name
        obteinedUser.sex            = jsonUser?.sex
        obteinedUser.phone          = jsonUser?.phone
        obteinedUser.locationId     = jsonUser?.location_id
        obteinedUser.picture        = jsonUser?.profile_picture
        obteinedUser.dateOfBirth    = new Date().parse("MM-dd-yyyy",jsonUser?.date_of_birth)
        obteinedUser.dateUpdate     = new Date()
        obteinedUser.password       = jsonUser?.password


        if(!obteinedUser.validate()){

            obteinedUser.errors.allErrors.each {
                responseMessage += MessageFormat.format(it.defaultMessage, it.arguments) + " "
            }
            throw new BadRequestException(responseMessage)

        }

        obteinedUser.save()

        jsonResult = getResult(obteinedUser)

        jsonResult
    }

    def searchUser(params){

        Map jsonResult  = [:]
        def queryMap    = [:]
        def userEmail
        def userPassword
        def tokenAdmin

        def SEARCH_PARAMS_MAP =[
                email:"email",
                password: "password",
                admin:"admin",
        ]

        params.each { key, value ->

            def newKey = SEARCH_PARAMS_MAP[key]

            if (newKey){

                queryMap[newKey]=value

                if (newKey=='email'){
                    userEmail = value
                }
                if (newKey == 'password'){
                    userPassword = value
                }
                if (newKey == 'admin'){

                    tokenAdmin = value
                }

            }
        }

        if (!queryMap){
            throw new BadRequestException("Bad Request call not found params")
        }


        tokenAdminValid(tokenAdmin)


        def userCriteria = User.createCriteria()
        def result = userCriteria.list() {

            eq('email', userEmail)
            eq('password', userPassword)

        }

        if (result.size() == 0){
            throw new NotFoundException("The User not Found")
        }
        def userId
        result.each{
            userId = it.id
        }

        jsonResult.user_id = userId

        jsonResult


    }

    def tokenAdminValid(def token){

        if(!token){
            throw new BadRequestException("Access Invalid, Admin not found")
        }

        if (token != 'MB-ADMIN_123456KKAADPZ'){
            throw new NotFoundException("Access admin = "+token+" is not found")
        }

    }

    def getResult(def userResult){

        Map jsonResult=[:]

        jsonResult.id                   = userResult.id
        jsonResult.name                 = userResult.name
        jsonResult.email                = userResult.email
        jsonResult.password             = userResult.password
        jsonResult.phone                = userResult.phone
        jsonResult.location_id          = userResult.locationId
        jsonResult.date_of_birth        = userResult.dateOfBirth
        jsonResult.registration_date    = userResult.dateRegistered
        jsonResult.date_last_update     = userResult.dateUpdate
        jsonResult.date_deleted         = userResult.dateDeleted
        jsonResult.origin               = userResult.origin
        jsonResult.profile_picture      = userResult.picture
        jsonResult.sex                  = userResult.sex
        jsonResult.status               = userResult.status
        jsonResult.user_type            = userResult.userType

        jsonResult
    }

    /*

    def accessUser(def email, def password){

        def obteinedUser = User.findByEmailAndPassword(email, password)
        def token
        def codigo
        def access

        if (obteinedUser != null)
        {
            token = java.net.URLEncoder.encode(obteinedUser.id+'')
            token = token.encodeAsEncripcion()
            codigo = 'Valid Acces Token'
        }
        else{
            token = ''
            codigo = 'Invalid Acces Token'
        }

        access = [
                'token': token,
                'codigo':codigo
        ]

        return access

    }
    */

}
