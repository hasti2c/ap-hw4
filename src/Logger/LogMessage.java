package Logger;

public class LogMessage {
    private LogType type;
    private String time, text;

    LogMessage(LogType type, String time, String text) {
        this.type = type;
        this.time = time;
        this.text = text;
    }

    @Override
    public String toString() {
        return type + " " + time + " " + text;
    }
}
