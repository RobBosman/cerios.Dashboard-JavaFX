@echo off

:: set JAVA_HOME and MAVEN_HOME
call ..\env.cmd

call %MAVEN_HOME%\bin\mvn -ff clean package eclipse:clean eclipse:m2eclipse -Dmaven.test.skip=true
call %MAVEN_HOME%\bin\mvn -ff test

pause