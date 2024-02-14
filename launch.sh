#!/bin/bash
cd ci-server
mvn package -DskipTests
mvn exec:java -D exec.mainClass=com.group1.App