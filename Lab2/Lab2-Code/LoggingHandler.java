import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

/**
 * Logging component that writes all show events to a log file.
 * This component subscribes to the same EV_SHOW event as ClientOutput,
 * following the implicit-invocation pattern. It does NOT modify ClientOutput.
 */
@SuppressWarnings("deprecation")
public class LoggingHandler implements Observer {

    private static final String LOG_FILE = "system.log";
    private SimpleDateFormat dateFormat;
    private PrintWriter writer;

    /**
     * Constructs a logging handler.
     * Subscribes to EV_SHOW events at creation.
     * Opens the log file in overwrite mode (fresh log per run).
     */
    public LoggingHandler() {
        EventBus.subscribeTo(EventBus.EV_SHOW, this);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            // Open file in append mode so logs are preserved across runs.
            this.writer = new PrintWriter(new FileWriter(LOG_FILE, true));
            // Register a shutdown hook to close the writer when the JVM exits.
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (writer != null) {
                    writer.close();
                }
            }));
        } catch (IOException e) {
            System.err.println("Error: Opening log file: " + e.getMessage());
        }
    }

    /**
     * Event handler. On receiving a show event, writes the message to the log file.
     *
     * @param event an event object. (caution: not to be directly referenced)
     * @param param a parameter object of the event. (to be cast to appropriate data type)
     */
    public void update(Observable event, Object param) {
        if (writer != null) {
            String timestamp = dateFormat.format(new Date());
            writer.println("[" + timestamp + "] " + (String) param);
            writer.flush();
        }
    }
}
