@echo off

set JAVA_HOME=C:\Progs\Java\jdk1.6.0_14
set MAVEN_REPO=C:\Home\Rob\Documenten\Werk\Dashboard\IDE\Maven\maven-repo
::set JAVA_HOME=D:\Progs\Java\jdk1.6.0_12
::set MAVEN_REPO=D:\IDE\kik-samenstellen\Maven\maven-repo

set CLASSPATH=
set CLASSPATH=%CLASSPATH%;valori-space\target\valori-space-0.0.1-SNAPSHOT.jar
set CLASSPATH=%CLASSPATH%;dashboard-model\target\dashboard-model-0.0.1-SNAPSHOT.jar
set CLASSPATH=%CLASSPATH%;dashboard-serialize\target\dashboard-serialize-0.0.1-SNAPSHOT.jar
set CLASSPATH=%CLASSPATH%;%MAVEN_REPO%\com\thoughtworks\xstream\xstream\1.3.1\xstream-1.3.1.jar
set CLASSPATH=%CLASSPATH%;%MAVEN_REPO%\org\codehaus\jettison\jettison\1.1\jettison-1.1.jar

set EXECCLASS=nl.valori.space.SpaceSerializerJSON

"%JAVA_HOME%\bin\java" -cp %CLASSPATH% %EXECCLASS% %*

pause