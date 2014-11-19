package users

/**
 * Created by dpaz on 07/11/14.
 */
import org.bouncycastle.util.IPAddress
import javax.servlet.http.HttpServletResponse
import org.joda.time.format.DateTimeParser
import users.exceptions.ConflictException
import users.exceptions.BadRequestException
import groovyx.net.http.*
import org.codehaus.groovy.grails.web.util.WebUtils
import org.codehaus.groovy.grails.commons.DefaultGrailsApplication


class ValidAccess{

    def PERMISSIONS_MAP = [
            admin:"A",
            normal:"P,AIPM,MIPM",
            developer:"P,AIPM,MIPM,AIPA",
            dealer:"P,AIPM,MIPM"
    ]

    def validAccessToken(def accessToken){

        def origin
        def tokenEncry
        def userIdA
        def userIdB
        def status
        def dayYear
        def userType

        def parts = accessToken.split('_')

        if (!parts){

            throw new ConflictException("The access_token is not valid")
        }
        try {

            origin      = parts[0]
            tokenEncry  = parts[1]
            userIdA     = parts[2]

        }catch(Exception e){
            throw new ConflictException("The access_token is not valid")
        }


        def tokenDecry = tokenEncry.decodeSecure().toString()


        def partsB = tokenDecry.split('-')

        if (!partsB){

            throw new ConflictException("The access_token is not valid")
        }

        try {

            userIdB = partsB[0]
            status = partsB[1]
            dayYear = partsB[2]
            userType = partsB[3]

        }catch(Exception e){

            throw new ConflictException("The access_token is not valid")

        }



        if (userIdA != userIdB){

           throw new ConflictException("The access_token is not valid")
        }

        if (status != 'active'){

           throw new ConflictException("The user is not active")
        }

        def cal             = Calendar.instance
        def dayYearToday    = cal.get(Calendar.DAY_OF_YEAR)



        if (dayYear != dayYearToday.toString()){

            throw new ConflictException("The access_token is expired")
        }

        def permissions = PERMISSIONS_MAP[userType]


        if(!permissions){

            throw new ConflictException("The access_token is not valid permissions deneid")
        }

        userType
    }

    def isInternal(){

        def grailsApplication = new DefaultGrailsApplication()

        def ipValid = grailsApplication.config.ipValid

        def result = false
        def request = WebUtils.retrieveGrailsWebRequest().getCurrentRequest()

        String remoteAddress = request.getRemoteAddr()
        InetAddress inetAddress = InetAddress.getByName(remoteAddress)
        if (inetAddress instanceof Inet6Address) {

            if (remoteAddress == ipValid){
                result = true
            }

        } else {
            if (remoteAddress == ipValid){
                result = true
            }

        }

        result
    }


}
