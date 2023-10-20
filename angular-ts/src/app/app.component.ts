import { EvaluationClientService } from './../../openapi/api/evaluationClient.service';
import {Component, OnInit} from '@angular/core';
import {
  ApiClientAnswer,
  ApiClientAnswerSet,
  ApiClientSubmission,
  ApiEvaluationInfo,
  ApiEvaluationStatus,
  ApiUser,
  LoginRequest, LogService,
  QueryResult,
  QueryResultLog, SubmissionService,
  SuccessfulSubmissionsStatus,
  SuccessStatus, UserService
} from '../../openapi';
import {Settings} from './settings.model';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'DRES Client Example: Angular/TypeScript';


  // === SETUP ===
  // See app.module.ts import declaration of @NgModule annotation
  constructor(
    private userService: UserService,
    private evaluationClientService: EvaluationClientService,
    private submissionService: SubmissionService,
    private logService: LogService
  ) {
  }

  ngOnInit(): void {

    // === Handshake / Login ===
    this.userService.postApiV2Login({
      username: Settings.user,
      password: Settings.pass
    } as LoginRequest)
    .subscribe((login: ApiUser) => {
      this.println('Login successful\n' +
        `user: ${login.username}\n` +
        `role: ${login.role}` +
        `session: ${login.sessionId}`);

      // Successful login

      /*
      It is better pratice, to let the browser properly handle
      cookies. In order to to that, uncomment the "withCredentials"
      in the app.module.ts line to not have to worry about the session.
       */
      const sessionId = login.sessionId;

      let evalutationId = "";

      // Wait for a second (do other things)
      setTimeout(() => {
        // === Example 1: Evaluation Run Info ===
        this.evaluationClientService.getApiV2ClientEvaluationList (sessionId).subscribe((evaluations: ApiEvaluationInfo[]) => {
          this.println(`Found ${evaluations.length} ongoing evaluation runs`);
          evaluations.forEach((evaluation: ApiEvaluationInfo) => {
            this.println(`${evaluation.name} (${evaluation.id}): ${evaluation.status}`);
            if (evaluation.templateDescription) {
              this.println(evaluation.templateDescription);
              this.println('');
            }
            if(evaluation.status == ApiEvaluationStatus.ACTIVE) {
              evalutationId = evaluation.id;
            }
          });
        });

        this.submissionService.postApiV2SubmitByEvaluationId(evalutationId, {answerSets: [
          {answers: [
            {
              text: null, //text - in case the task is not targeting a particular content object but plaintext
              mediaItemName: '00001', // item -  item which is to be submitted
              mediaItemCollectionName: null, // collection - does not usually need to be set
              start: 10_000, //start time in milliseconds
              end: null //end time in milliseconds, in case an explicit time interval is to be specified
            } as ApiClientAnswer
          ]} as ApiClientAnswerSet
        ]} as ApiClientSubmission,
        
        sessionId).subscribe((submissionResponse: SuccessfulSubmissionsStatus) => {
          // Check if submission as successful

          if (submissionResponse && submissionResponse?.status) {
            this.println('The submission was sccuessfully sent to the server.');

            // === Example 3: Log ===
            this.logService.postApiV2LogResult(sessionId, {
              timestamp: Date.now(),
              sortType: 'list',
              results: [
                {item: 'some_item_name', segment: 3, score: 0.9, rank: 1} as QueryResult,
                {item: 'some_item_name', segment: 5, score: 0.85, rank: 2} as QueryResult,
                {item: 'some_other_item_name', segment: 12, score: 0.76, rank: 3} as QueryResult
              ],
              events: [],
              resultSetAvailability: ''
            } as QueryResultLog);

            // Assume no errors during log sending

            // Some more stuff happens, we'll just sleep
            setTimeout(() => {
              // === Example 5: Gracefuly logout ===
              this.userService.getApiV2Logout(sessionId).subscribe((logout: SuccessStatus) => {
                if (logout.status) {
                  this.println('Successfully logged out');
                } else {
                  this.println('Error during logout: ' + logout.description);
                }
              });
            }, 1000);
          }
        }, error => {
          if (error.status === 401) {
            console.error('There was an authentication error during the submission. Check the session id.');
          } else if (error.status === 404) {
            console.error('There is currently no active task which would accept submissions.');
          } else {
            console.error(`Something unexpected went wrong during the submission: : ${JSON.stringify(error)}`);
          }
        });

      }, 1000);
    }, error => {
      console.error('Could not connect / authenticate');
    });
  }

  private println(msg: string): void {
    console.log(msg);
  }


}
