@echo off
setlocal
set "CLOUDFLARED=C:\Users\skql3\Documents\Codex\tools\cloudflared\cloudflared.exe"
if not exist "%CLOUDFLARED%" (
  echo cloudflared.exe not found.
  pause
  exit /b 1
)
echo Aftertime must already be running at http://localhost:8080
echo Keep this window open. Closing it will close the public link.
echo.
"%CLOUDFLARED%" tunnel --url http://127.0.0.1:8080 --protocol http2 --no-autoupdate

