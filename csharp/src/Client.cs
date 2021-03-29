using System;
using System.Collections;
using System.Collections.Generic;
using System.Threading;
using Dev.Dres.Client.Api;
using Dev.Dres.Client.Client;
using Dev.Dres.Client.Model;

namespace csharp
{
  public class Client
  {
    static void RunExample()
    {
      var config = new Configuration
      {
        BasePath = Settings.BasePath
      };

      // === Setup ===
      
      // Initialize user api client
      var userApi = new UserApi(config);
      
      // initialize evaluation run info client
      var runInfoApi = new ClientRunInfoApi(config);
      
      // initialize submission api client
      var submissionApi = new SubmissionApi(config);
      
      // initialize logging api client
      var logApi = new LogApi(config);
      
      // === Handshake ===

      Println($"Try to log in to {config.BasePath} with user {Settings.User}");
      
      // Login request
      UserDetails login = null;
      try
      {
        login = userApi.PostApiLogin(new LoginRequest(Settings.User, Settings.Pass));
      }
      catch(ApiException ex)
      {
        Console.Error.WriteLine("Could not log in due to exception: "+ex.Message);
        return;
      }

      // Login successful
      Println($"Successfully logged in.\n" +
              $"user: '{login.Username}'\n" +
              $"role: '{login.Role}'\n" +
              $"session: '{login.SessionId}'");
      
      // Store session token for future requests
      var sessionid = login.SessionId;
      
      Thread.Sleep(1000);
      
      // === Example 1: Evaluation Runs Info ===
      
      // Retrieve current evaluation runs
      var currentRuns = runInfoApi.GetApiRuninfoList(sessionid);
      Println($"Found {currentRuns.Runs.Count} ongoing evaluation runs");
      currentRuns.Runs.ForEach(info =>
      {
        Println($"{info.Name} ({info.Id}): {info.Status}");
        if (info.Description != null)
        {
          Println(info.Description);
        }

        Println();
      });

      // === Example 2: Submission ===
      // Setup Submission
      SuccessfulSubmissionsStatus submissionResponse = null;
      try
      {
        submissionResponse = submissionApi.GetSubmit(
          session: sessionid,
          collection: null, //does not usually need to be set
          item: "some_item_name",//item which is to be submitted
          frame: null,// for items with temporal components, such as video
          shot: null,// only one of the time fields needs to be set.
          timecode: "00:00:10:00"//in this case, we use the timestamp in the form HH:MM:SS:FF
        );

      }
      catch (ApiException ex)
      {
        switch (ex.ErrorCode)
        {
          case 401:
            Console.Error.WriteLine("There was an authentication error during submission. Check the session id.");
            break;
          case 404:
            Console.Error.WriteLine("There is currently no active task which would accept submissions.");
            break;
          default:
            Console.Error.WriteLine($"Something unexpected went wrong during the submission: '{ex.Message}'.");
            return;
        }
      }

      if (submissionResponse != null && submissionResponse.Status)
      {
        Println("The submission was successfully sent to the server.");
        
        // === Example 3: Log ===
        logApi.PostLogResult(
          session: sessionid,
          new QueryResultLog(DateTimeOffset.Now.ToUnixTimeMilliseconds(),
            sortType: "list",
            results: CreateResultList(),
            events: new List<QueryEvent>(),
            resultSetAvailability: "")
        );
      }
      
      // === Graceful logout ===
      Thread.Sleep(1000);

      var logout = userApi.GetApiLogout(sessionid);

      if (logout.Status)
      {
        Println("Successfully logged out");
      }
      else
      {
        Println("Error during logout "+logout.Description);
      }

    }

    /// <summary>
    /// Dummy data for example log
    /// </summary>
    /// <returns></returns>
    private static List<QueryResult> CreateResultList()
    {
      List<QueryResult> list = new List<QueryResult>();
      list.Add(new QueryResult("some_item_name", segment: 3, score: 0.9, rank: 1));
      list.Add(new QueryResult("some_item_name", segment: 5, score: 0.85, rank: 2));
      list.Add(new QueryResult("some_ohter_item_name", segment: 12, score: 0.76, rank: 3));

      return list;
    }
    
    private static void Println(String msg = "")
    {
      Console.Out.WriteLine(msg);
    }
  }
}