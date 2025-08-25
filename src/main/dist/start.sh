#!/bin/bash

# Start script for Spring Boot application
JAR_FILE="my-test-project-1.0-SNAPSHOT.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "Error: JAR file $JAR_FILE not found!"
    exit 1
fi

echo "Starting Spring Boot application..."
java -Dlog4j.configurationFile=log4j2.xml -jar "$JAR_FILE" "$@"
