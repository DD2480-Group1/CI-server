#!/bin/bash
echo "Building frontend for ci-server..."
cd ci-server/src/main/webapp/ci-frontend
echo "Running 'npm install'"
npm install
echo "Running 'npm run build'"
npm run build
cd ../../../..

echo "Building backend for ci-server..."
mvn clean install -DskipTests