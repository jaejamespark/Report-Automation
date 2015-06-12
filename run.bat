@echo off
echo /////////////////////////////////////////////////////
echo Hello~~ This is "TD ANALYZER TOOL". Hopefully, this will do the work for MM
echo so we can all go home on time :) No more staying late because of the reports :p
echo ////////////////////////////////////////////////////
echo [Limtation / Warning:]
echo Due to its logic, this program compares only the first 6 characters of each model name. 
echo If the first 6 characters of the model name are same, it is considered to be the same model.
echo Due to this, it may produce wrong result. So please check the output as necessary when using this tool.
echo ////////////////////////////////////////////////////
echo.
echo Tip: Press "Tab" key to find desired input file that is in this current folder.
echo.
echo Please enter input excel file names with its extension. If you have multiple input files, you can enter them with a space in between. 
echo (ex: H810_issues.xls H950_issues.xls) 
set /p filename=""
echo
javac -cp jxl.jar; nvg/mm/td/ExtractData.java
java -cp jxl.jar;. nvg.mm.td.ExtractData %filename%

pause