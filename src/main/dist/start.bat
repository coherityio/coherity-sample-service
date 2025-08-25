@echo off

REM Start script for Spring Boot application
set JAR_FILE=my-test-project-1.0-SNAPSHOT.jar

if not exist "%JAR_FILE%" (
    echo Error: JAR file %JAR_FILE% not found!
    exit /b 1
)

echo Starting Spring Boot application...
java -Dlog4j.configurationFile=log4j2.xml -jar "%JAR_FILE%" %*
