@echo off
set "JAVA_HOME=C:\Users\skql3\.jdks\ms-21.0.12"
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d "%~dp0backend"
java -jar build\libs\aftertime-api-0.0.1-SNAPSHOT.jar

