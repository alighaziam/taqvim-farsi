@echo off
set APP_HOME=%~dp0
if exist "%APP_HOME%gradle\wrapper\gradle-wrapper.jar" (
  java -classpath "%APP_HOME%gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
  exit /b %ERRORLEVEL%
)
where gradle >nul 2>nul
if %ERRORLEVEL%==0 (
  gradle -p "%APP_HOME%" %*
  exit /b %ERRORLEVEL%
)
echo Gradle Wrapper JAR is missing and system Gradle is not installed. Install Gradle 8.13 and run: gradle -p "%APP_HOME%" ^<task^>
exit /b 1
