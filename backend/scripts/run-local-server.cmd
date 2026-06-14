@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "BACKEND_DIR=%SCRIPT_DIR%.."
set "RUNTIME_DIR=%BACKEND_DIR%\target\local-run"
set "STDOUT_FILE=%RUNTIME_DIR%\starter.stdout.log"
set "STDERR_FILE=%RUNTIME_DIR%\starter.stderr.log"

if not exist "%RUNTIME_DIR%" mkdir "%RUNTIME_DIR%"

echo [%date% %time%] launch cmd wrapper>>"%STDOUT_FILE%"
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT_DIR%run-local-server.ps1" 1>>"%STDOUT_FILE%" 2>>"%STDERR_FILE%"
