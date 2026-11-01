@echo off
title Campus Circular Market - Java Spring Boot Server
echo ========================================================
echo  Campus Circular Market - Spring Boot & MySQL Backend
echo ========================================================
echo.

where java >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Java was not found in your system PATH.
    echo Please install Java 17 (or newer) and ensure JAVA_HOME is set.
    echo Download: https://adoptium.net/temurin/releases/?version=17
    echo.
    pause
    exit /b 1
)

echo Java detected!
java -version
echo.
echo Starting Spring Boot application on http://localhost:8080 ...
echo (Ensure MySQL server is running on localhost:3306 with database 'campus_market_db')
echo.

call mvnw.cmd spring-boot:run
if %ERRORLEVEL% neq 0 (
    echo.
    echo Spring Boot build failed. If you have Maven installed, you can also run:
    echo mvn spring-boot:run
)
pause

