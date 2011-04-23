package org.bd.survey

import com.google.appengine.api.users.UserService
import org.springframework.social.facebook.FacebookTemplate
import org.bd.survey.utils.Utils

class LoginController {
    UserService googleUserService
    def persistenceManager
    def mailService

    def index = {
        if (!session.user) {
            redirect(action:choose)
        } else {
            redirect(uri: '/')
        }
    }

    def choose = {
        // TODO add options other than Google
    }

    def logout = {
        session.user = null
        session.isAdminUser = false
        session.invalidate()
        redirect(uri: '/')
    }

    def loginByGoogle = {
        def redirectTo = params.redirectTo ?: '/'
        response.sendRedirect(googleUserService.createLoginURL("/login/processLoginByGoogle?redirectTo=${redirectTo.encodeAsURL()}"))
        return
    }

    def processLoginByGoogle = {
        def redirectTo = params.redirectTo ?: '/'
        if(googleUserService.isUserLoggedIn()) {

            if (googleUserService.isUserAdmin()) {
                session.isAdminUser = true
            }

            com.google.appengine.api.users.User googleUser = googleUserService.getCurrentUser()
            if (!session.user || session.user?.email != googleUser.email) {
                def list = persistenceManager.newQuery( AppUser, "email == '${googleUser.email}'" ).execute()
                AppUser appUser
                if (list?.size()) {
                    appUser = list.first()
                } else {
                    appUser = new AppUser(email:googleUser.email, firstName:googleUser.nickname, lastName:'', origin:UserOrigin.GOOGLE, originId:googleUser.userId)
                    persistenceManager.makePersistent(appUser)
                    mailService.sendMail("Account created by ${googleUser.toString()}")
                }
                session.user = appUser
            }
            response.sendRedirect(redirectTo)
            return
        } else {
            redirect(action:loginByGoogle, params:['redirectTo': redirectTo])
            return
        }
    }

    def loginByFacebook = {
        println "in loginByFacebook"
        def immRedirect = redirectAbsUrlForFacebook()
        println "final redir: ${immRedirect.encodeAsURL()}"
        def facebookLoginUrl = "https://graph.facebook.com/oauth/authorize?client_id=${grailsApplication.config.facebook.app.id}&redirect_uri=${immRedirect.encodeAsURL()}"
        response.sendRedirect(facebookLoginUrl)
        return
    }

    def processLoginByFacebook = {
        if (params.code) {
            def immRedirect = redirectAbsUrlForFacebook()
            def facebookAuthTokenUrl = "https://graph.facebook.com/oauth/access_token?client_id=${grailsApplication.config.facebook.app.id}&client_secret=${grailsApplication.config.facebook.app.secret}&code=${params.code.encodeAsURL()}&redirect_uri=${immRedirect.encodeAsURL()}"

            URL url = new URL(facebookAuthTokenUrl);
            URLConnection connection = url.openConnection();
//            connection.setConnectTimeout(10000);
//            connection.setReadTimeout(10000);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))
            byte[] data = Utils.readBytes(connection.getInputStream())
            String responseData = new String(data)
            if (responseData) {
                println("${responseData}")
                def queryMap = [:]
                responseData?.split('&')?.each {
                    if (it?.contains('=')) {
                        String[] div = it?.split('=')
                        queryMap."${div[0]}" = div[1]
                    }
                }
                if (queryMap.access_token) {
                    println "Authentication successful! Access token found in response body ${queryMap.access_token}"
                    FacebookTemplate template = new FacebookTemplate(queryMap.access_token)
                    println "Profile ID: ${template?.profileId}"
                }
            }
        }
        redirect(uri:'/')
    }

    def redirectAbsUrlForFacebook() {
        def redirectUri = params.redirectTo ?: '/'
        return createLink(uri:"/login/processLoginByFacebook?redirectTo=${redirectUri.encodeAsURL()}", absolute:true)
    }
}
