@echo off
setlocal enabledelayedexpansion

:: ===== CONFIG =====
set MAIN_CLASS=com.bestbudz.Server
set SRC_DIR=src
set BIN_DIR=bin
set LIB_DIR=lib
set DATA_DIR=data
set PACK_DIR=BestBudzServerPack
set TAR_NAME=%PACK_DIR%.tar.gz
:: ==================
echo.
echo 🔧 Building classpath...
set "CP="
for %%J in ("%LIB_DIR%\*.jar") do (
    set CP=!CP!;%%J
)
:: Trim leading semicolon
set CP=!CP:~1!

echo.
echo 🔄 Cleaning...
rmdir /s /q "%PACK_DIR%" >nul 2>&1
mkdir "%BIN_DIR%"
mkdir "%PACK_DIR%"

echo.
echo 🧵 Indexing Java source files...
dir /s /b "%SRC_DIR%\*.java" > sources.txt

echo.
echo 🛠️ Compiling Java sources...
javac -d "%BIN_DIR%" -cp "!CP!" -sourcepath "%SRC_DIR%" @sources.txt
del sources.txt

if errorlevel 1 (
    echo ❌ Compilation failed.
    pause
    exit /b
)


echo.
echo 📁 Copying runtime files...
xcopy /E /I /Y "%BIN_DIR%" "%PACK_DIR%\bin" >nul
xcopy /E /I /Y "%DATA_DIR%" "%PACK_DIR%\data" >nul
xcopy /E /I /Y "%LIB_DIR%" "%PACK_DIR%\lib" >nul

echo.
echo 🧰 Creating Linux launcher...
(
echo #!/bin/bash
echo cd "$(dirname "$0")"
echo java --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.desktop/java.awt.font=ALL-UNNAMED -Xms128m -Xmx2048m -cp "bin:lib/*" com.bestbudz.Server
) > "%PACK_DIR%\run.sh"

echo.
echo 🔒 Making run.sh executable...
bash -c "chmod +x \"%PACK_DIR%/run.sh\""

echo.
echo 📦 Creating tarball for server deploy...
tar -czf "%TAR_NAME%" "%PACK_DIR%"

echo.
echo ✅ Server pack ready: %TAR_NAME%
pause
