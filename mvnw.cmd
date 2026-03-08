@ECHO OFF
where mvn >NUL 2>NUL
IF %ERRORLEVEL% EQU 0 (
  mvn %*
  EXIT /B %ERRORLEVEL%
)
ECHO Maven is not installed. Install Maven 3.9+ or generate a full Maven Wrapper before running this build.
EXIT /B 1
