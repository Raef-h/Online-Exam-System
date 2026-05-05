package server;

import common.*;
import java.io.*;
import java.net.*;
import java.time.*;
import java.util.*;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final DatabaseManager db;
    private final List<Exam> activeExams;
    private final ExamServer server;

    private String studentName;
    private boolean active = true;

    public ClientHandler(Socket socket, DatabaseManager db,
                         List<Exam> activeExams, ExamServer server) {
        this.socket = socket;
        this.db = db;
        this.activeExams = activeExams;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in  = new ObjectInputStream(socket.getInputStream());

            // 1. Receive student name
            Message msg = (Message) in.readObject();
            if (msg.getType() != Message.Type.CONNECT) { sendError("Bad handshake"); return; }
            studentName = ((String) msg.getData()).trim();
            server.log("Student connected: " + studentName + " from " + socket.getInetAddress());
            LogManager.log("STUDENT_JOIN: " + studentName);

            // 2. Send active exams
            List<ExamInfo> infoList = buildExamInfoList();
            send(new Message(Message.Type.EXAM_LIST, (java.io.Serializable) infoList));

            // 3. Receive exam selection
            Message selMsg = (Message) in.readObject();
            if (selMsg.getType() != Message.Type.SELECT_EXAM) { sendError("Expected exam selection"); return; }
            int selectedId = (Integer) selMsg.getData();

            Exam exam = findExam(selectedId);
            if (exam == null) { sendError("Exam not found"); return; }

            server.log(studentName + " selected exam: " + exam.getExamName());
            LogManager.log("EXAM_SELECT: " + studentName + " -> " + exam.getExamName());

            // 4. Check if already completed
            if (db.hasCompleted(studentName, exam.getExamId())) {
                sendError("You have already completed this exam."); return;
            }

            // 5. Send exam info (no questions)
            Exam examWithoutQs = new Exam(exam.getExamId(), exam.getExamName(), exam.getYear(), exam.getSemester(), exam.getStartDateTime(), new ArrayList<>());
            send(new Message(Message.Type.EXAM_DATA, examWithoutQs));

            // 6. Enter question loop
            int[] progress = db.getProgress(studentName, exam.getExamId());
            int currentIndex = progress[0];
            int currentScore = progress[1];
            
            List<Question> questions = exam.getQuestions();
            int totalQuestions = questions.size();

            while (currentIndex < totalQuestions) {
                // Check if time expired
                long minutes = Duration.between(exam.getStartDateTime(), LocalDateTime.now()).toMinutes();
                if (minutes >= 60) {
                    sendError("Exam time has expired (60 minutes).");
                    break;
                }

                Question q = questions.get(currentIndex);
                QuestionDTO dto;
                if (q instanceof MCQ) {
                    MCQ mcq = (MCQ) q;
                    dto = new QuestionDTO("MCQ", mcq.getQuestionText(), currentIndex, totalQuestions);
                    dto.setChoices(mcq.getChoiceA(), mcq.getChoiceB(), mcq.getChoiceC(), mcq.getChoiceD());
                } else {
                    dto = new QuestionDTO("TF", q.getQuestionText(), currentIndex, totalQuestions);
                }

                send(new Message(Message.Type.NEXT_QUESTION, dto));

                // Wait for answer
                Message ansMsg = (Message) in.readObject();
                if (ansMsg.getType() != Message.Type.SUBMIT_ANSWER) {
                    sendError("Expected answer submission");
                    return;
                }
                
                String answer = (String) ansMsg.getData();
                if (q.getCorrectAnswer().equalsIgnoreCase(answer.trim())) {
                    currentScore++;
                }
                
                currentIndex++;
                db.saveProgress(studentName, exam.getExamId(), currentIndex, currentScore);
            }

            // 7. Grade and finalize if completed
            if (currentIndex >= totalQuestions) {
                Result result = new Result(studentName, exam.getExamId(), exam.getExamName(),
                                           currentScore, totalQuestions);
                db.saveResult(result, exam);
                server.log(studentName + " scored " + currentScore + "/" + totalQuestions);
                LogManager.log("EXAM_COMPLETE: " + studentName + " score=" + currentScore);

                send(new Message(Message.Type.RESULT, result));
            }

        } catch (EOFException | SocketException e) {
            server.log("Student disconnected: " + (studentName != null ? studentName : "unknown"));
        } catch (Exception e) {
            server.log("Error handling client: " + e.getMessage());
            e.printStackTrace();
        } finally {
            close();
            server.removeClient(this);
        }
    }

    private List<ExamInfo> buildExamInfoList() {
        List<ExamInfo> list = new ArrayList<>();
        synchronized (activeExams) {
            for (Exam e : activeExams) {
                long minutes = Duration.between(e.getStartDateTime(), LocalDateTime.now()).toMinutes();
                if (minutes < 60) {
                    list.add(new ExamInfo(e.getExamId(), e.getExamName(),
                                          e.getStartDateTime(), e.getQuestions().size()));
                }
            }
        }
        return list;
    }

    private Exam findExam(int id) {
        synchronized (activeExams) {
            for (Exam e : activeExams) { if (e.getExamId() == id) return e; }
        }
        return null;
    }

    private void send(Message msg) throws IOException {
        out.writeObject(msg);
        out.flush();
    }

    private void sendError(String msg) {
        try { send(new Message(Message.Type.ERROR, msg)); } catch (IOException ignored) {}
    }

    public void close() {
        active = false;
        try { socket.close(); } catch (IOException ignored) {}
    }

    public String getStudentName() { return studentName; }
}
