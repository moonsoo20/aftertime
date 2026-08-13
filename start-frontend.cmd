@echo off
set "NODE_DIR=C:\Users\skql3\Documents\Codex\tools\nodejs\node-v24.18.1-win-x64"
set "PATH=%NODE_DIR%;%PATH%"
cd /d "%~dp0frontend"
call "%NODE_DIR%\npm.cmd" install
if errorlevel 1 exit /b %errorlevel%
call "%NODE_DIR%\npm.cmd" run dev

