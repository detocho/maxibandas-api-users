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

    def searchUser(){

        def jsonResult = []
        def userResult = User.findAll()

        if (!userResult){
            throw new NotFoundException("Users not found")
        }

        userResult.each{
            jsonResult.add(

                    id                   : it.id,
                    name                 : it.name,
                    email                : it.email,
                    password             : it.password,
                    phone                : it.phone,
                    location_id          : it.locationId,
                    date_of_birth        : it.dateOfBirth,
                    registration_date    : it.dateRegistered,
                    date_last_update     : it.dateUpdate,
                    date_deleted         : it.dateDeleted,
                    origin               : it.origin,
                    profile_picture      : it.picture,
                    sex                  : it.sex,
                    status               : it.status,
                    user_type            : it.userType

            )
        }

        jsonResult


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
