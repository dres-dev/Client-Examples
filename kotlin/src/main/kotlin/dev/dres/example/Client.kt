package dev.dres.example

import dev.dres.client.*
import org.openapitools.client.infrastructure.ClientException
import org.openapitools.client.models.*
import java.lang.RuntimeException
import java.net.ConnectException

object Client {

    @JvmStatic
    fun main(args: Array<String>) {

        //initialize user api client
        val userApi = UserApi(Settings.basePath)

        //initialize evaluation run info client
        val runInfoApi = EvaluationClientApi(Settings.basePath)

        //initialize submission api client
        val submissionApi = SubmissionApi(Settings.basePath)

        //initialize logging api client
        val logApi = LogApi(Settings.basePath)

        println("Trying to log in to '${Settings.basePath}' with user '${Settings.user}'")

        //login request
        val login = try {
            userApi.postApiV2Login(
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

        println(
            """
            login successful
            user: '${login.username}'
            role: '${login.role?.value}'
            session: ${login.sessionId}
        """.trimIndent()
        )

        //store session token for future requests
        val sessionId = login.sessionId!!


        Thread.sleep(1000)

        val currentEvaluations = runInfoApi.getApiV2ClientEvaluationList(sessionId)

        println("Found ${currentEvaluations.size} ongoing evaluation runs")
        currentEvaluations.forEach {
            println("${it.name} (${it.id}): ${it.status}")
            if (it.templateDescription != null) {
                println(it.templateDescription)
            }
            println()
        }

        val evaluationId = currentEvaluations.find { it.status == ApiEvaluationStatus.aCTIVE }?.id ?: throw RuntimeException("No active evaluation")

        val successStatus = try{
            submissionApi.postApiV2SubmitByEvaluationId(
                evaluationId,
                ApiClientSubmission(
                    listOf(
                        ApiClientAnswerSet(
                            listOf(
                                ApiClientAnswer(
                                    text = null, //in case the task is not targeting a particular content object but plaintext
                                    mediaItemName = "some_item_name", //item which is to be submitted
                                    mediaItemCollectionName = null, //does not usually need to be set
                                    start = 10_000, //start time in milliseconds
                                    end = null //end time in milliseconds, in case an explicit time interval is to be specified
                                )
                            )
                        )
                    )
                ),
                sessionId
            )
        } catch (clientException: ClientException) {
            when (clientException.statusCode) {
                401 -> System.err.println("There was an authentication error during the submission. Check the session id.")
                404 -> System.err.println("There is currently no active task which would accept submissions.")
                412 -> System.err.println("The submission was rejected by the server: ${clientException.message}")
                else -> System.err.println("Something unexpected went wrong during the submission: '${clientException.message}'.")
            }
            null
        }

        if (successStatus != null && successStatus.status) {
            println("The submission was successfully sent to the server.")

            logApi.postApiV2LogResultByEvaluationId(
                evaluationId,
                session = sessionId,
                QueryResultLog(
                    System.currentTimeMillis(),
                    sortType = "list",
                    results = listOf(
                        RankedAnswer(
                            ApiClientAnswer(mediaItemName = "some_item_name", start = 1000, end = 1000), 1
                        ),
                        RankedAnswer(
                            ApiClientAnswer(mediaItemName = "some_item_name", start = 5000, end = 5000), 2
                        ),
                        RankedAnswer(
                            ApiClientAnswer(mediaItemName = "some_other_item_name", start = 12000, end = 12000), 3
                        ),
                    ),
                    events = listOf(),
                    resultSetAvailability = ""
                )
            )

        }

        Thread.sleep(1000) //doing other things...


        //log out
        val logout = userApi.getApiV2Logout(sessionId)

        if (logout.status) {
            println("Successfully logged out")
        } else {
            println("error during logout: ${logout.description}")
        }

    }

}