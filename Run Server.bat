@echo off
title BestBudz Server
color B
echo BestBudz is starting up...
for /f "tokens=1-4 delims=/ " %%i in ("%date%") do (
     set dow=%%h
     set month=%%i
     set day=%%j
     set year=%%k
   )
SET datestr=%month%_%day%_%year%
SET path=D:\BestBudzRSPS\
SET filename=%path%log-%datestr%.txt

echo ==LOG FILE %datestr% == >> %filename%

"C:\Program Files\Java\jdk1.8.0_321\bin\java.exe" -cp bin;lib/gson-2.2.2.jar;lib/gson-2.2.2-sources.jar;lib/json-lib-2.4-jdk15.jar;lib/netty-3.6.1.Final.jar;lib/xpp3_min-1.1.4c.jar;lib/xstream-1.3.1.jar; com.bestbudz.Server
pause