# DO NOT DELETE THIS BRANCH
This branch needs to exists for one of the unit tests in this framework to work.
The unit tests in question rely on being able to test a version of this repository that passes
all its tests correctly.

In order for the unit tests to be able to fetch the specific commit that is used for testing
this branch needs to remain open.

If this branch is deleted, the unit tests need to be changed such that the dummy requests
use a different commitSHA.
