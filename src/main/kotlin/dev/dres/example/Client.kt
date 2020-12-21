package dev.dres.example

import dev.dres.client.UserApi
import org.openapitools.client.infrastructure.ClientException
import org.openapitools.client.models.LoginRequest
import java.net.ConnectException

object Client {

    @JvmStatic
    fun main(args: Array<String>) {

        //initialize user api client
        val userApi = UserApi(Settings.basePath)

        println("Trying to log in to '${Settings.basePath}' with user '${Settings.user}'")

        //login request
        val login = try {
            userApi.postApiLogin(
                LoginRequest(
                    Settings.user, Settings.pass
                )
            )
        } catch (connectException: ConnectException) { //catch connection exception, it's sufficient to do this only once
            System.err.println("Could not connect to ${Settings.basePath}, exiting")
            return
        } catch (clientException: ClientException) { //catch authentication exception
            System.err.println("Error during login request: '${clientException.message}', exiting")
            return
        }

        println("""
            login successful
            user: '${login.username}'
            role: '${login.role.value}'
            session: ${login.sessionId}
        """.trimIndent())

        //store session token for future requests
        val sessionId = login.sessionId!!


        /*

        ... do other things ...

         */

        Thread.sleep(1000) //doing other things...


        //log out
        val logout = userApi.getApiLogout(sessionId)

        if (logout.status) {
            println("Successfully logged out")
        } else {
            println("error during logout: ${logout.description}")
        }

    }

}