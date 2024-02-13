# assignment-2

## Description

This is a continuous integration server that is designed to be used with a GitHub repository. The server will be able to receive webhooks from GitHub and run tests on the repository. The server will also be able to send notifications to the repository owner about the status of the tests.

A frontend is implemented to allow the user to view the status of the tests and the logs of the tests. The frontend is implemented using vue and the backend is implemented using jetty server.

## Prerequisites

* Java version 11 or higher
* Maven
* Ngrok (optional)

## How To Build

```
bash build.sh
```

## How to Run

```
bash launch.sh
```

## Use ngrok to expose the server to the internet

```
ngrok http --domain=<your-ngrok.domain> 8080
```

## How to Test

```bash
cd ci-server
mvn test
```

## Generate Documentation

```bash
cd ci-server
mvn javadoc:javadoc
```

The documentation will be generated in the `target/site/apidocs` directory and the dependencies in the `target/site` directory.

## Implementation Details

### Notification

## Commit Structure for assignment-2

Each commit message should consist of a subject and a body. Please follow this message structure when committing to the project:

**Subject**

* The first line (the title) is the subject and should contain imperative language, present tense: "Fix bug" not "Fixed bug".
* The subject should also start with a capital letter, not end on a period and be less than fifty characters.
* Prefix is not required as long as the subject summarizes the change itself.

After the subject, a blank line should be made followed by the body.

**Body**

* The body should also use imperative language: "Fix missing" instead of "Fixed missing" or "Fixes missing".
* When possible, use punctuation and capital letters where appropriate.
* Lines should not exceed 72 characters, except when including compiler error messages.
* Can include multiple paragraphs seperated by new lines.
* Try to always link to an issue, but if it is a really small change, it is not required.
* Any references to issues should be in the last paragraph, and use prefix like "Fixes", Closes" or "Resolves". See [GitHub Keywords](https://docs.github.com/en/issues/tracking-your-work-with-issues/linking-a-pull-request-to-an-issue) for more details.
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

Hannes

ZOU Hetai (Ed):

Elliot:

Yening:

## Essence
