echo off
REM *********************************
REM  FORCE Kill tomcat PROCESS - by name - pre-determined by tasklist |more;  tomcat must be a service for this.  
REM    ANT tasks, ant java execution of bootstrap or exec shutdown etc, etc DO NOT terminate tomcat gracefully - 
REM    so it holds on to logs, lucene etc - just force kill it
REM  Worst case scenario - if tomcat not a service - completely kill java.exe - 
REM    that is why you cannot use any java based tool like ANT, or scheduler like Jenkins - need shell scripts - in this case a batch file
REM
REM  After tomcat is dead, whack lucene, logs, recreate them
REM  
REM  Start the tomcat SERVICE (note again, kill the process, start the service)
REM  
REM  fire-up all the important websites - first call to nexus takes a full 30 seconds 
REM   this serves to test when running manually, scheduler does not care
REM
REM **********************************
echo on
taskkill /IM "ApacheTomcat7.0.30.exe" /F
timeout /t 10
rd /s /q c:\tomcat7.0.30\lucene\indexes
timeout /t 10
rd /s /q c:\tomcat7.0.30\logs
timeout /t 10
rd /s /q c:\logs\tomcat
timeout /t 10
md c:\tomcat7.0.30\lucene\indexes
md c:\tomcat7.0.30\logs
md c:\logs\tomcat
net start "Apache Tomcat 7.0"
timeout /t 10
start http://localhost:8080/nexus/needs
timeout /t 60
start http://localhost:8080/nexus/daps
timeout /t 10
start http://localhost:8080/nexus/paws
timeout /t 10
start http://localhost:8080/nexus/orgs
timeout /t 10
start http://localhost:8080/nexus/bibs/Delaware
timeout /t 30
start http://localhost:8080/pawz
timeout /t 30
start http://localhost:8080/daps
timeout /t 30
start http://localhost:8080/orgs
timeout /t 30
start http://localhost:8080/asmts
timeout /t 30
start http://localhost:8080/outlooks
timeout /t 30
start http://localhost:8080/climatologies
timeout /t 30
start http://localhost:8080/scenarios
timeout /t 30
taskkill /IM "iexplore.exe" /F