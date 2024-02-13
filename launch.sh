#!/bin/bash
cd ci-server
mvn package
mvn exec:java -D exec.mainClass=com.group1.App