@echo off
chcp 65001 >nul
cd /d "%~dp0punch-clock"

echo Starting punch-clock...
set ELECTRON_EXE=%~dp0punch-clock\node_modules\electron\dist\electron.exe
set APP_DIR=%~dp0punch-clock

if not exist "%ELECTRON_EXE%" (
    echo ERROR: electron.exe not found
    pause
    exit /b 1
)

start "" "%ELECTRON_EXE%" "%APP_DIR%"
exit
