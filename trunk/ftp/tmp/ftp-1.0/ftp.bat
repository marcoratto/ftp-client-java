@echo off
setlocal EnableDelayedExpansion
set JAVA_OPTS=-Xms256m -Xmx1024m -XX:MaxPermSize=120m

for /f %%i in ("%0") do set FTP_HOME=%%~dpi
set FTP_HOME=%FTP_HOME:~0,-1%

set LIB_DIR=%FTP_HOME%\lib
set RES_DIR=%FTP_HOME%\res
set BIN_DIR=%FTP_HOME%\bin

set CPATH=%BIN_DIR%\ftp.jar
set CPATH=%RES_DIR%;%CPATH%
for /R %FTP_HOME%\lib %%a in (*.jar) do (
   set CPATH=!CPATH!;%%a
)

:checkJava
set _JAVACMD=%JAVACMD%

if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\java.exe
goto runScp

:noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=java.exe
 
:runScp
"%_JAVACMD%" %JAVA_OPTS% -classpath "%CPATH%" -Dftp_config_file="%RES_DIR%\scp_config_file.properties" uk.co.marcoratto.ftp.Runme %*
endlocal
