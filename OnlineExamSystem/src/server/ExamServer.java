package server;

import common.*;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.function.Consumer;
import javafx.application.Platform;

public class ExamServer {

    private ServerSocket serverSocket;
    private final int port;
    private volatile boolean running = false;

    private final DatabaseManager db;
    private final List<Exam> activeExams = new ArrayList<>();
    private final List<ClientHandler> activeClients = new ArrayList<>();

    private Consumer<String> onLog;

    public ExamServer(int port, DatabaseManager db) {
        this.port = port;
        this.db = db;
    }

    public void setOnLog(Consumer<String> cb) { this.onLog = cb; }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        log("Server started on port " + port);

        Thread t = new Thread(() -> {
            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    log("New connection: " + client.getInetAddress().getHostAddress());
                    ClientHandler handler = new ClientHandler(client, db, activeExams, this);
                    synchronized (activeClients) { activeClients.add(handler); }
                    new Thread(handler).start();
                } catch (IOException e) {
                    if (running) log("Accept error: " + e.getMessage());
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void stop() {
        running = false;
        try { if (serverSocket != null) serverSocket.close(); } catch (IOException ignored) {}
        synchronized (activeClients) {
            activeClients.forEach(ClientHandler::close);
            activeClients.clear();
        }
        log("Server stopped.");
    }

    public void addExam(Exam exam) {
        synchronized (activeExams) { activeExams.add(exam); }
        log("Exam added: " + exam.getExamName());
    }

    public List<Exam> getActiveExams() {
        synchronized (activeExams) { return new ArrayList<>(activeExams); }
    }

    public List<ClientHandler> getActiveClients() {
        synchronized (activeClients) { return new ArrayList<>(activeClients); }
    }

    public void removeClient(ClientHandler h) {
        synchronized (activeClients) { activeClients.remove(h); }
    }

    public void log(String msg) {
        LogManager.log(msg);
        if (onLog != null) Platform.runLater(() -> onLog.accept(msg));
    }

    public boolean isRunning() { return running; }
}
