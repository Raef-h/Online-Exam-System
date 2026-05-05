package server;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogManager {
    private static final String LOG_FILE = "exam_log.txt";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static PrintWriter writer;

    static {
        try {
            writer = new PrintWriter(new FileWriter(LOG_FILE, true));
        } catch (IOException e) {
            System.err.println("Cannot open log file: " + e.getMessage());
        }
    }

    public static synchronized void log(String message) {
        String line = "[" + LocalDateTime.now().format(FMT) + "] " + message;
        System.out.println(line);
        if (writer != null) { writer.println(line); writer.flush(); }
    }

    public static void close() {
        if (writer != null) writer.close();
    }
}
