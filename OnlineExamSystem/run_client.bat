@echo off
setlocal
set JAVAFX=..\JavaFX\javafx-sdk-26\lib
set MODS=javafx.controls,javafx.fxml
set SRC_COMMON=src\common\Question.java src\common\QuestionDTO.java src\common\MCQ.java src\common\TF.java src\common\Exam.java src\common\ExamInfo.java src\common\Result.java src\common\Message.java src\common\StudentProgress.java
set SRC_CLIENT=src\client\ClientApp.java src\client\LoginController.java src\client\ExamListController.java src\client\ExamController.java

echo [1/3] Cleaning output...
if exist out_client rmdir /s /q out_client
mkdir out_client

echo [2/3] Compiling...
javac --module-path "%JAVAFX%" --add-modules %MODS% ^
      -d out_client ^
      %SRC_COMMON% %SRC_CLIENT%

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)

echo [3/3] Copying resources...
xcopy /s /y src\resources out_client\

echo.
echo [OK] Build successful! Running client...
echo.
java --module-path "%JAVAFX%" --add-modules %MODS% ^
     -cp "out_client" ^
     client.ClientApp

pause
