package server;

import common.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.io.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "onlineexam";
    private static final String USER = "root";
    private static final String PASS = "";
    private Connection conn;

    public DatabaseManager() throws SQLException {
        try (Connection setupConn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement s = setupConn.createStatement()) {
            s.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
        }
        
        conn = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASS);
        createTables();
        clearSession();
    }

    private void clearSession() throws SQLException {
        try (Statement s = conn.createStatement()) {
            s.execute("DELETE FROM EXAMS");
            s.execute("DELETE FROM PROGRESS");
            s.execute("DELETE FROM ExamResults");
        }
    }

    private void createTables() throws SQLException {
        try (Statement s = conn.createStatement()) {


            s.execute("CREATE TABLE IF NOT EXISTS EXAMS (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "`name` VARCHAR(255), " +
                "`year` VARCHAR(255), " +
                "`semester` VARCHAR(255), " +
                "start_time TIMESTAMP, " +
                "question_count INT, " +
                "questions_blob LONGBLOB)");

            s.execute("CREATE TABLE IF NOT EXISTS ExamResults (" +
                "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                "`Student ID` VARCHAR(255), " +
                "`Course Name` VARCHAR(255), " +
                "`Year` VARCHAR(255), " +
                "`Semester` VARCHAR(255), " +
                "`Score` INT)");

            s.execute("CREATE TABLE IF NOT EXISTS PROGRESS (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "student_name VARCHAR(255), " +
                "exam_id INT, " +
                "current_question INT DEFAULT 0, " +
                "current_score INT DEFAULT 0, " +
                "UNIQUE(student_name, exam_id))");

            try {
                s.execute("ALTER TABLE EXAMS ADD COLUMN questions_blob LONGBLOB");
            } catch (SQLException ignored) {} 
        }
    }

    public synchronized int saveExam(Exam exam) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement(
                "INSERT INTO EXAMS (`name`, `year`, `semester`, start_time, question_count, questions_blob) VALUES (?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, exam.getExamName());
            p.setString(2, exam.getYear());
            p.setString(3, exam.getSemester());
            p.setTimestamp(4, Timestamp.valueOf(exam.getStartDateTime()));
            p.setInt(5, exam.getQuestions().size());
            
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(exam.getQuestions());
                p.setBytes(6, baos.toByteArray());
            } catch (IOException e) {
                throw new SQLException("Serialization failed", e);
            }

            p.executeUpdate();
            try (ResultSet rs = p.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public synchronized List<Exam> getAllExams() throws SQLException {
        List<Exam> list = new ArrayList<>();
        try (Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM EXAMS")) {
            while (rs.next()) {
                Exam e = new Exam();
                e.setCourseName(rs.getString("name"));
                e.setYear(rs.getString("year"));
                e.setSemester(rs.getString("semester"));
                e.setExamId(rs.getInt("id"));
                e.setStartDateTime(rs.getTimestamp("start_time").toLocalDateTime());
                
                byte[] blob = rs.getBytes("questions_blob");
                if (blob != null) {
                    try (ByteArrayInputStream bais = new ByteArrayInputStream(blob);
                         ObjectInputStream ois = new ObjectInputStream(bais)) {
                        @SuppressWarnings("unchecked")
                        List<Question> questions = (List<Question>) ois.readObject();
                        e.setQuestions(questions);
                    } catch (Exception ex) {
                        System.err.println("Failed to load questions for exam " + e.getExamId());
                    }
                }
                list.add(e);
            }
        }
        return list;
    }

    public synchronized void saveResult(Result result, Exam exam) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement(
                "INSERT INTO ExamResults (`Student ID`, `Course Name`, `Year`, `Semester`, `Score`) VALUES(?,?,?,?,?)")) {
            p.setString(1, result.getStudentName());
            p.setString(2, exam.getExamName());
            p.setString(3, exam.getYear());
            p.setString(4, exam.getSemester());
            p.setInt(5, result.getScore());
            p.executeUpdate();
        }
    }

    public synchronized void saveProgress(String studentName, int examId, int currentQuestion, int currentScore) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement(
                "INSERT INTO PROGRESS (student_name, exam_id, current_question, current_score) VALUES(?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE current_question=?, current_score=?")) {
            p.setString(1, studentName);
            p.setInt(2, examId);
            p.setInt(3, currentQuestion);
            p.setInt(4, currentScore);
            p.setInt(5, currentQuestion);
            p.setInt(6, currentScore);
            p.executeUpdate();
        }
    }

    public synchronized int[] getProgress(String studentName, int examId) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement(
                "SELECT current_question, current_score FROM PROGRESS WHERE student_name=? AND exam_id=?")) {
            p.setString(1, studentName);
            p.setInt(2, examId);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return new int[]{rs.getInt(1), rs.getInt(2)};
                }
            }
            return new int[]{0, 0};
        }
    }

    public synchronized boolean hasCompleted(String studentName, int examId) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement(
                "SELECT COUNT(*) FROM ExamResults WHERE `Student ID`=? AND `Course Name`=(SELECT name FROM EXAMS WHERE id=?)")) {
            p.setString(1, studentName);
            p.setInt(2, examId);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public synchronized int getCompletedCount(String examName) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement(
                "SELECT COUNT(*) FROM ExamResults WHERE `Course Name`=?")) {
            p.setString(1, examName);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }



    public void close() {
        try { if (conn != null && !conn.isClosed()) conn.close(); }
        catch (SQLException ignored) {}
    }
}
