package users

import java.text.MessageFormat
import org.apache.ivy.plugins.conflict.ConflictManager
import grails.converters.*
import users.exceptions.NotFoundException
import users.exceptions.ConflictException
import users.exceptions.BadRequestException



class UserService {

    static transactional = 'mongo'
    def validAccess = new ValidAccess()


    def getUser(def params){

        Map jsonResult=[:]

        if (!params.userId) {
            throw new NotFoundException("You must provider user_id")
        }

        def userResult = User.findById(params.userId)

        if (!userResult){
            throw new NotFoundException("The user with user_id = "+params.userId+" not found")
        }

        def access_token

        if (params.access_token) {

            access_token = validAccess.validAccessToken(params.access_token)
            def user_id = params.access_token.split('_')[2]
            if(user_id != params.userId){
               throw new ConflictException("Your token  invalid for this user")
            }

        }

        jsonResult = getResult(userResult, access_token)

        jsonResult


    }


    def createUser(def jsonUser){

        Map jsonResult=[:]
        def responseMessage = ''

        User newUser = new User(

                name:       jsonUser?.name ? jsonUser.name : "",
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

        jsonResult = getResult(newUser, null)

        jsonResult

    }

    def modifyUser(def params, def jsonUser){

        Map jsonResult = [:]
        def responseMessage = ''

        if (!params.access_token){

            throw new BadRequestException ("You must provider de access_token")

        }

        if (!params.userId) {
            throw new NotFoundException("You most provider userid")
        }

        def access_token = validAccess.validAccessToken(params.access_token)
        def user_id = params.access_token.split('_')[2]
        if(user_id != params.userId){
            throw new ConflictException("Your token  invalid for this user")
        }

        def obteinedUser = User.findById(params.userId)

        if (!obteinedUser){
            throw new NotFoundException("The User with userId="+params.userId+" not found")
        }

        //TODO debemos agregar un validaro de json

        //if (user_type == "admin_mp") {
        //obteinedUser.dealerId = jsonUser?.dealer_id
        //obteinedUser.email = jsonUser?.email
        //obteinedUser.origin = jsonUser?.origin
        //obteinedUser.status = jsonUser?.status
        //obteinedUser.userType = jsonUser?.user_type
        //}

        if (jsonUser?.email){

            throw new BadRequestException("Error: Parameter email is not modifiable")
        }



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

        jsonResult = getResult(obteinedUser, access_token)

        jsonResult
    }

    def searchUser(def params, def jsonUser){

        Map jsonResult  = [:]

        def userEmail       = jsonUser?.email
        def userPassword    = jsonUser?.password
        def tokenAdmin      = params.admin

        tokenAdminValid(tokenAdmin)

        if (!userEmail){
            throw new BadRequestException("You must provider the email of user")
        }
        if(!userPassword){
            throw new BadRequestException("You must provider the password of user ")
        }


        def userCriteria = User.createCriteria()
        def result = userCriteria.list() {

            eq('email', userEmail)
            eq('password', userPassword)

        }

        if (result.size() == 0){
            throw new NotFoundException("The User not Found")
        }
        def userId
        def userType
        def status
        result.each{

            userId      = it.id
            userType    = it.userType
            status       = it.status

        }

        jsonResult.user_id      = userId
        jsonResult.user_type    = userType
        jsonResult.status       = status

        jsonResult


    }

    def searchUserByEmail(def params){

        Map jsonResult  = [:]


       /* if(!validAccess.isInternal()){
            throw new ConflictException("Not valid method")
        }*/

        def userEmail       = params.email



        if (!userEmail){
            throw new BadRequestException("You must provider the email of user")
        }



        def userCriteria = User.createCriteria()
        def result = userCriteria.list() {
            eq('email', userEmail)

        }

        if (result.size() == 0){
            throw new NotFoundException("The User not Found")
        }
        def userId
        def userType
        def status
        def email
        def name
        result.each{

            userId      = it.id
            userType    = it.userType
            status      = it.status
            email       = it.email
            name        = it.name

        }

        jsonResult.user_id      = userId
        jsonResult.user_type    = userType
        jsonResult.status       = status
        jsonResult.email        = email
        jsonResult.name         = name


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


    def getResult(def userResult, def access_token){

        Map jsonResult=[:]

        jsonResult.id                   = userResult.id
        jsonResult.name                 = userResult.name ? userResult.name : ""

        if (access_token || validAccess.isInternal()) {

        jsonResult.email                = userResult.email
        //jsonResult.password             = userResult.password
        jsonResult.phone                = userResult.phone

        }
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



}
