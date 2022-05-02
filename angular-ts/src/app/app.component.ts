import {Component, OnInit} from '@angular/core';
import {UserService} from '../../openapi/dev/dres/client/user.service';
import {ClientRunInfoService} from '../../openapi/dev/dres/client/clientRunInfo.service';
import {SubmissionService} from '../../openapi/dev/dres/client/submission.service';
import {LogService} from '../../openapi/dev/dres/client/log.service';
import {
  ClientRunInfo,
  ClientRunInfoList,
  LoginRequest,
  QueryResult,
  QueryResultLog,
  SuccessfulSubmissionsStatus,
  SuccessStatus,
  UserDetails
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
    private runInfoService: ClientRunInfoService,
    private submissionService: SubmissionService,
    private logService: LogService
  ) {
  }

  ngOnInit(): void {

    // === Handshake / Login ===
    this.userService.postApiV1Login({
      username: Settings.user,
      password: Settings.pass
    } as LoginRequest)
    .subscribe((login: UserDetails) => {
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

      // Wait for a second (do other things)
      setTimeout(() => {
        // === Example 1: Evaluation Run Info ===
        this.runInfoService.getApiV1ClientRunInfoList(sessionId).subscribe((currentRuns: ClientRunInfoList) => {
          this.println(`Found ${currentRuns.runs.length} ongoing evaluation runs`);
          currentRuns.runs.forEach((run: ClientRunInfo) => {
            this.println(`${run.name} (${run.id}): ${run.status}`);
            if (run.description) {
              this.println(run.description);
              this.println('');
            }
          });
        });

        // === Example 2: Submission ===
        this.submissionService.getApiV1Submit(
          null, // collection - does not usually need to be set
          'some_item_name', // item -  item which is to be submitted
          null, //text - in case the task is not targeting a particular content object but plaintext
          null, // frame - for items with temporal components, such as video
          null, // shot - only one of the time fields needs to be set.
          '00:00:10:00', // timecode - in this case, we use the timestamp in the form HH:MM:SS:FF
          sessionId // the sessionId, as always
        ).subscribe((submissionResponse: SuccessfulSubmissionsStatus) => {
          // Check if submission as successful

          if (submissionResponse && submissionResponse?.status) {
            this.println('The submission was sccuessfully sent to the server.');

            // === Example 3: Log ===
            this.logService.postApiV1LogResult(sessionId, {
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
              this.userService.getApiV1Logout(sessionId).subscribe((logout: SuccessStatus) => {
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
