@echo off
setlocal
set JAVAFX=..\JavaFX\javafx-sdk-26\lib
set H2=lib\h2-2.2.224.jar;lib\mysql-connector-j-8.4.0.jar
set MODS=javafx.controls,javafx.fxml
set SRC_COMMON=src\common\Question.java src\common\QuestionDTO.java src\common\MCQ.java src\common\TF.java src\common\Exam.java src\common\ExamInfo.java src\common\Result.java src\common\Message.java src\common\StudentProgress.java
set SRC_SERVER=src\server\LogManager.java src\server\DatabaseManager.java src\server\ClientHandler.java src\server\ExamServer.java src\server\MonitorController.java src\server\ServerController.java src\server\ServerApp.java

echo [1/3] Cleaning output...
if exist out rmdir /s /q out
mkdir out

echo [2/3] Compiling...
javac --module-path "%JAVAFX%" --add-modules %MODS% ^
      -cp "%H2%" ^
      -d out ^
      %SRC_COMMON% %SRC_SERVER%

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)

echo [3/3] Copying resources...
xcopy /s /y src\resources out\

echo.
echo [OK] Build successful! Running server...
echo.
java --module-path "%JAVAFX%" --add-modules %MODS% ^
     -cp "out;%H2%" ^
     server.ServerApp

pause
