# DD2480 - CI

## Description

This is a continuous integration server that is designed to be used with a GitHub repository. The server will be able to receive webhooks from GitHub and run tests on the repository. The server will also be able to send notifications to the repository owner about the status of the tests.

A frontend is implemented to allow the user to view the status of the tests and the logs of the tests. The frontend is implemented using vue and the backend is implemented using jetty server.

## Prerequisites
Before downloading the source code and attempting to build the project, ensure that you have met the following requirements:

* Java version 11 or higher
* Maven
* npm
* Ngrok (optional)

## How To build frontend and backend
You can use the provided shell file to build the project after cloning it:
```
bash build.sh
```

## How to start CI server
You can use the provided shell file to start the server
```
bash launch.sh
```

## Use ngrok to expose the server to the internet
You will need to port-forward/tunneling with ngrok if you want to access the website outside localhost, run the following command to use ngrok:
```
ngrok http --domain=<your-ngrok.domain> 8080
```

If this is done, then some URLs may have to be changed in order to make the server run correctly, these URLs are located in the `App.java` file and `URLconfig`. The URL will be provided by ngrok, and you will have to set up a webhook as well on GitHub.

## How to manually run tests on CI server
```bash
cd ci-server
mvn test
```

## Generate Documentation
To generate javadoc HTML documentation, run the following command:
```bash
cd ci-server
mvn javadoc:javadoc
```

The documentation will be generated in the `target/site/apidocs` directory and the dependencies in the `target/site` directory.

## Implementation Details

### Compilation
#### Implementation
The server compiles the project by running the function `compileRepository`. If the repository clone is successful, the server compiles the repository through ProcessBuilder. The ProcessBuilder takes a string command as input and executes it in the terminal. This command uses Maven to compile the cloned repo, and the output of the console is returned as a string. The function has a try-statement that catches any errors during compilation. If an error is detected, it returns a `CompileException` and stops the compile. If the build compiles without any errors, the console output is returned, and the program can continue.

#### Unit testing
How the unit tests work is that they check if the handler fetches correctly and returns an OK response. statuscode (200). A dummy HTTP request for a past commit that is structurally correct and can compile and run is sent to a temporary host on the server. After the server is done, the unit test sends a request to the server and checks what the response status code is. It then asserts that it is equal to OK (200).

### Testing
#### Implementation
Tests are executed via the `junit` extension. After compiling was successful, the CI testing process was started by a command run on the server through ProcessBuilder. Unit tests are implemented in the `AppTest.Java` file, with multiple tests testing different categories of the project. The console output is captured and returned when all tests are finished. Using regex, the number of failed tests is counted and checked to see if the commit passed all unit tests or not.

#### Unit testing
Testing the unit tests was interpreted as just verifying that the unit tests can actually run and return expected values. This was achieved by having two unit tests: one that asserts if it is true and one that asserts if it is false. The values are set directly and are just there to test that the unit tests actually run and behave as expected in their simplest form.

### Notification
#### Implementation
The notification was implemented using the GitHub API specifically:

* Commit status: the CI server sets the [commit status](https://help.github.com/articles/about-statuses/) on the repository ([REST API](https://developer.github.com/v3/repos/statuses/) for Github).

When a commit is pushed, the server connects to the GitHub API to create a status POST and initially sets the status to `pending`. If the commit fails due to compilation errors or unit testing, the status is updated to `error` respectively `failure`. If the commit passes all tests, the status is likewise updated to reflect this with `success`. A link to the CI frontend website can also be found in the GitHub status **details** link, providing with a shortcut to find more information about the commit.

#### Unit testing
The unit test for notification is done in a similar manner to the compile test. Firstly the commit status is manually set to `failure`. After this, the test gets the commit status from the GitHub API and checks that it has actually been set to `failure` there. Afterward, the project is compiled with a dummy HTTP request that should set the commit status to `success`. Lastly, the commit status is requested from the GitHub API and asserted to see that it is actually `success`.

## Commit Structure for DD2480 - CI

Each commit message should consist of a subject and a body. Please follow this message structure when committing to the project:

**Subject**

* The first line (the title) is the subject and should contain imperative language, present tense: "Fix bug" not "Fixed bug".
* The subject should also start with a capital letter, not end on a period, and be less than fifty characters.
* Prefix is not required as long as the subject summarizes the change itself.

After the subject, a blank line should be made followed by the body.

**Body**

* The body should also use imperative language: "Fix missing" instead of "Fixed missing" or "Fixes missing".
* When possible, use punctuation and capital letters where appropriate.
* Lines should not exceed 72 characters, except when including compiler error messages.
* Can include multiple paragraphs separated by new lines.
* Try to always link to an issue, but if it is a really small change, it is not required.
* Any references to issues should be in the last paragraph, and use prefixes like "Fixes", Closes" or "Resolves". See [GitHub Keywords](https://docs.github.com/en/issues/tracking-your-work-with-issues/linking-a-pull-request-to-an-issue) for more details.
* Use "Fixes" for bugs and "Closes" for general issues.

Here is an example of a good commit:

```
Add missing import of 'gtest' in src/decide.cpp

decide/src/decide.cpp wont compile since the 'gtest',
dependency is not found.

Fix introduced by adding 'gtest' module to CMakeList.txt.

Fixes #1002
Closes #64
```

## Statement of Contributions

ZOU Hetai (Ed):
* Worked on the frontend of the server, implemented several frontend components and HTTP requests.
* Debugging on frontend and backend.
* Contributed to reviewing pull requests and issues.
* Setup the build and documentation system of the project.

Elliot:
* Worked on the backend of the server, implemented major parts of the system such as the cloning and compiling system, notification system, file save system and helper functions associated with them.
* Bug fixing for the backend.
* Contributed to managing and creating issues, tags for issues and reviewing colleagues' pull requests.
* Contributed to the README file and other documentation.
* Set up the token system.
* Wrote Essence.

Hannes:
* TODO

Yening:
* TODO

## Essence Team State
Most of the points have been achieved with some exceptions. In *seeded* we have achieved most of the relevant points except `governance rules`.  The point `leadership structure` can be a bit debatable since we decided not to have any leader but rather a collaborative effort take on assignments. Looking at the next category, *formed*, we have struggled a bit with some of the points, like `all team members understand how to perform their work` because of switching tools from C++ and CMAKE to Java and Maven. But in general, the team has mostly fulfilled all the points with the exception being `external collaborators`, since there are none. In regard to *collaborating*, we have achieved all the points to differing degrees. We sit together parallel and work in parallel as well as actively book meetings and communicate via our discord server. *Performing* points such as no backtracking, duplicate work, and reworking have so far been achieved, with each feature only needing cases some minor bug fixes. However, `the team consistently meets its commitments` and `identifies and addresses problems` can be a bit debatable since development time can at times be slow. It is often that major features are mostly done, but there might have popped up some additional helper feature, required for the major feature to work, which slows down development. Lastly, the points in `adjourning` have not been fulfilled and do not seem relevant to this assignment. 

To improve our effectiveness as a team, and to amend some of the points, we have to get better at identifying the specifications of a problem and outlining what needs to be done more thoughtfully. We also need to keep in mind to try and help all team members get comfortable with the tools. Lastly, there might be a need for a more structured way of working since a lot of the work is not done independently right now, but on voice calls. But, the method has thus far been successful but time-consuming.
