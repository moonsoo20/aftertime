@echo off
setlocal
set "NODE_DIR=C:\Users\skql3\Documents\Codex\tools\nodejs\node-v24.18.1-win-x64"
set "JAVA_HOME=C:\Users\skql3\.jdks\ms-21.0.12"
set "PATH=%NODE_DIR%;%JAVA_HOME%\bin;%PATH%"

cd /d "%~dp0frontend"
call "%NODE_DIR%\npm.cmd" install
if errorlevel 1 exit /b %errorlevel%
call "%NODE_DIR%\npm.cmd" run build
if errorlevel 1 exit /b %errorlevel%

cd /d "%~dp0backend"
call gradlew.bat bootJar --no-daemon
if errorlevel 1 exit /b %errorlevel%

echo.
echo Build complete: backend\build\libs\aftertime-api-0.0.1-SNAPSHOT.jar
