package Logger;

import Broker.Monitor;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.LongFunction;

public class Logger extends Thread {
    private Queue<LogMessage> logMessages = new LinkedList<>();
    private final Monitor logMonitor = new Monitor();
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss");
    private PrintWriter writer;
    private static Map<String, Logger> loggerInstances = new HashMap<>();

    private Logger(String topic) {
        try {
            writer = new PrintWriter(topic + ".log");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        start();
    }

    public static Logger getInstance(String topic) {
        if (!loggerInstances.containsKey(topic))
            loggerInstances.put(topic, new Logger(topic));
        return loggerInstances.get(topic);
    }

    @Override
    public void run() {
        while (logMessages.size() == 0) {
            logMonitor.doWait();
            logMonitor.clear();
        }
        while (logMessages.size() > 0) {
            log(logMessages.remove());
        }
        run();
    }

    private void log(LogMessage message) {
        synchronized (logMonitor) {
            writer.println(message);
            writer.flush();
        }
    }

    private String getTime() {
        return dtf.format(LocalDateTime.now());
    }

    public void requestLog(LogType type, String text) {
        synchronized (logMonitor) {
            logMessages.add(new LogMessage(type, getTime(), text));
            logMonitor.doNotify();
        }
    }
}
