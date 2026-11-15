@echo off
setlocal
cd /d "%~dp0"
echo ==========================================
echo       SmartQueue - Local Run Helper
echo ==========================================
echo.
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0run-smartqueue.ps1"
if errorlevel 1 (
  echo.
  echo SmartQueue could not start. Read the message above.
  pause
  exit /b 1
)
pause
