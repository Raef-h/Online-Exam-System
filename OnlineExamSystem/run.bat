@echo off
setlocal
set JAVAFX=javafx-sdk\lib
set MODS=javafx.controls,javafx.fxml
set H2=lib\mysql-connector-j-8.4.0.jar

set SRC_COMMON=src\common\Question.java src\common\QuestionDTO.java src\common\QuestionMCQ.java src\common\QuestionTF.java src\common\Exam.java src\common\ExamInfo.java src\common\Result.java src\common\Message.java
set SRC_SERVER=src\server\LogManager.java src\server\DatabaseManager.java src\server\ClientHandler.java src\server\ExamServer.java src\server\ServerController.java src\server\NewExamController.java
set SRC_CLIENT=src\client\LoginController.java src\client\ExamListController.java src\client\ExamController.java
set SRC_APP=src\app\MainApp.java src\app\ModeSelectionController.java

echo [1/3] Cleaning output...
if exist out rmdir /s /q out
mkdir out

echo [2/3] Compiling...
javac --module-path "%JAVAFX%" --add-modules %MODS% ^
      -cp "%H2%" ^
      -d out ^
      %SRC_COMMON% %SRC_SERVER% %SRC_CLIENT% %SRC_APP%

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)

echo [3/3] Copying resources...
xcopy /s /y src\resources out\ > nul

echo.
echo [OK] Build successful! Running Application...
echo.
java --module-path "%JAVAFX%" --add-modules %MODS% ^
     -cp "out;%H2%" ^
     app.MainApp

pause
