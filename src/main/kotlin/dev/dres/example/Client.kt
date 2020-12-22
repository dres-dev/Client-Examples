package dev.dres.example

import dev.dres.client.SubmissionApi
import dev.dres.client.UserApi
import org.openapitools.client.infrastructure.ClientException
import org.openapitools.client.models.LoginRequest
import java.net.ConnectException

object Client {

    @JvmStatic
    fun main(args: Array<String>) {

        //initialize user api client
        val userApi = UserApi(Settings.basePath)

        //initialize user api client
        val submissionApi = SubmissionApi(Settings.basePath)

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

        Thread.sleep(1000)

        val submissionResponse =
                try {
                    submissionApi.getSubmit(
                            session = sessionId,
                            collection = null, //does not usually need to be set
                            item = "some_item_name", //item which is to be submitted
                            frame = null, // for items with temporal components, such as video
                            shot = null,  // only one of the time fields needs to be set.
                            timecode = "00:00:10:00" //in this case, we use the timestamp in the form HH:MM:SS:FF
                    )
                } catch (clientException: ClientException) {
                    when (clientException.statusCode) {
                        401 -> System.err.println("There was an authentication error during the submission. Check the session id.")
                        404 -> System.err.println("There is currently no active task which would accept submissions.")
                        else -> System.err.println("Something unexpected went wrong during the submission: '${clientException.message}'.")
                    }
                    null
                }

        if (submissionResponse != null && submissionResponse.status) {
            println("The submission was successfully sent to the server.")
        }

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