package dev.dres.example;

import dev.dres.ApiClient;
import dev.dres.ApiException;
import dev.dres.client.ClientRunInfoApi;
import dev.dres.client.LogApi;
import dev.dres.client.SubmissionApi;
import dev.dres.client.UserApi;
import org.openapitools.client.model.*;

import java.net.ConnectException;
import java.util.Collections;
import java.util.List;

class Client {

    public static void main(String[] args) {

        ApiClient client = new ApiClient().setBasePath(Settings.BASE_PATH);

        //initialize user api client
        UserApi userApi = new UserApi(client);

        //initialize evaluation run info client
        ClientRunInfoApi runInfoApi = new ClientRunInfoApi(client);

        //initialize submission api client
        SubmissionApi submissionApi = new SubmissionApi(client);

        //initialize logging api client
        LogApi logApi = new LogApi(client);

        System.out.println("Trying to log in to '" + Settings.BASE_PATH + "' with user '" + Settings.USER + "'");

        //login request
        UserDetails login = null;
        try {
            login = userApi.postApiV1Login(new LoginRequest().username(Settings.USER).password(Settings.PASS));
        } catch (ApiException e) {

            if (e.getCause() instanceof ConnectException) {
                System.err.println("Could not connect to " + Settings.BASE_PATH + ", exiting");
            } else {
                System.err.println("Error during login request: '" + e.getMessage() + "', exiting");
            }
            return;
        }

        System.out.println("login successful");
        System.out.println("user: " + login.getUsername());
        System.out.println("role: " + login.getRole().getValue());
        System.out.println("session: " + login.getSessionId());

        //store session token for future requests
        String sessionId = login.getSessionId();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }

        ClientRunInfoList currentRuns = null;
        try {
            currentRuns = runInfoApi.getApiV1ClientRunInfoList(sessionId);
        } catch (ApiException e) {
            System.err.println("Error during request: '" + e.getMessage() + "', exiting");
            return;
        }

        System.out.println("Found " + currentRuns.getRuns().size() + " ongoing evaluation runs");

        for (ClientRunInfo run : currentRuns.getRuns()) {
            System.out.println(run.getName() + " (" + run.getId() + "): " + run.getStatus());
            if (run.getDescription() != null) {
                System.out.println(run.getDescription());
            }
            System.out.println();
        }

        SuccessfulSubmissionsStatus submissionResponse = null;
        try {
            submissionResponse = submissionApi.getApiV1Submit(
                    null, //does not usually need to be set
                    "some_item_name", //item which is to be submitted
                    null, //in case the task is not targeting a particular content object but plaintext
                    null, // for items with temporal components, such as video
                    null,  // only one of the time fields needs to be set.
                    "00:00:10:00", //in this case, we use the timestamp in the form HH:MM:SS:FF
                    sessionId
            );
        } catch (ApiException e) {
            switch (e.getCode()) {
                case 401: {
                    System.err.println("There was an authentication error during the submission. Check the session id.");
                    break;
                }
                case 404: {
                    System.err.println("There is currently no active task which would accept submissions.");
                    break;
                }
                default: {
                    System.err.println("Something unexpected went wrong during the submission: '" + e.getMessage() + "'.");
                }
            }
        }

        if (submissionResponse != null && submissionResponse.getStatus()) {
            System.out.println("The submission was successfully sent to the server.");

            try {
                logApi.postApiV1LogResult(
                        sessionId,
                        new QueryResultLog()
                                .timestamp(System.currentTimeMillis())
                                .sortType("list")
                                .results(
                                        List.of(
                                                new QueryResult().item("some_item_name").segment(3).score(0.9).rank(1),
                                                new QueryResult().item("some_item_name").segment(5).score(0.85).rank(2),
                                                new QueryResult().item("some_other_item_name").segment(12).score(0.76).rank(3)
                                        )
                                )
                                .events(Collections.emptyList())
                                .resultSetAvailability("")
                        );
            } catch (ApiException e) {
                System.err.println("Error during request: '" + e.getMessage() + "'");
            }

        }

        try {
            Thread.sleep(1000); //doing other things...
        } catch (InterruptedException ignored) {
        }

        SuccessStatus logout = null;

        try {
            logout = userApi.getApiV1Logout(sessionId);
        } catch (ApiException e) {
            System.err.println("Error during request: '" + e.getMessage() + "'");
        }

        if (logout != null && logout.getStatus()) {
            System.out.println("Successfully logged out");
        }

    }

}