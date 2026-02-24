import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {

    private static final String LOG_FILE = "system.log";

    public static void main(String[] args) {
        try {
            // Lookup all handler stubs from the Server registry on port 1100.
            Registry registry = LocateRegistry.getRegistry("localhost", 1100);

            IActivity listAllStudents = (IActivity) registry.lookup("ListAllStudents");
            IActivity listAllCourses = (IActivity) registry.lookup("ListAllCourses");
            IActivity listStudentsRegistered = (IActivity) registry.lookup("ListStudentsRegistered");
            IActivity listCoursesRegistered = (IActivity) registry.lookup("ListCoursesRegistered");
            IActivity listCoursesCompleted = (IActivity) registry.lookup("ListCoursesCompleted");
            IActivity registerStudent = (IActivity) registry.lookup("RegisterStudent");

            System.out.println("Connected to Server.");

            // Setup logging on the client side.
            PrintWriter logWriter = new PrintWriter(new FileWriter(LOG_FILE, true));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // Register a shutdown hook to close the log writer.
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (logWriter != null) {
                    logWriter.close();
                }
            }));

            // Create a buffered reader for user input.
            BufferedReader objReader = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                // Show available commands.
                System.out.println("\nStudent Registration System (RMI)\n");
                System.out.println("1) List all students");
                System.out.println("2) List all courses");
                System.out.println("3) List students who registered for a course");
                System.out.println("4) List courses a student has registered for");
                System.out.println("5) List courses a student has completed");
                System.out.println("6) Register a student for a course");
                System.out.println("x) Exit");
                System.out.print("\nEnter your choice and press return >> ");
                String sChoice = objReader.readLine().trim();

                String result = "";

                // Execute command 1: List all students.
                if (sChoice.equals("1")) {
                    result = listAllStudents.execute(null);
                    System.out.println("\n" + result);
                    log(logWriter, dateFormat, "List All Students:\n" + result);
                    continue;
                }

                // Execute command 2: List all courses.
                if (sChoice.equals("2")) {
                    result = listAllCourses.execute(null);
                    System.out.println("\n" + result);
                    log(logWriter, dateFormat, "List All Courses:\n" + result);
                    continue;
                }

                // Execute command 3: List students registered for a course.
                if (sChoice.equals("3")) {
                    System.out.print("\nEnter course ID and press return >> ");
                    String sCID = objReader.readLine().trim();
                    System.out.print("\nEnter course section and press return >> ");
                    String sSection = objReader.readLine().trim();

                    result = listStudentsRegistered.execute(sCID + " " + sSection);
                    System.out.println("\n" + result);
                    log(logWriter, dateFormat, "List Students Registered (" + sCID + " " + sSection + "):\n" + result);
                    continue;
                }

                // Execute command 4: List courses a student has registered for.
                if (sChoice.equals("4")) {
                    System.out.print("\nEnter student ID and press return >> ");
                    String sSID = objReader.readLine().trim();

                    result = listCoursesRegistered.execute(sSID);
                    System.out.println("\n" + result);
                    log(logWriter, dateFormat, "List Courses Registered (" + sSID + "):\n" + result);
                    continue;
                }

                // Execute command 5: List courses a student has completed.
                if (sChoice.equals("5")) {
                    System.out.print("\nEnter student ID and press return >> ");
                    String sSID = objReader.readLine().trim();

                    result = listCoursesCompleted.execute(sSID);
                    System.out.println("\n" + result);
                    log(logWriter, dateFormat, "List Courses Completed (" + sSID + "):\n" + result);
                    continue;
                }

                // Execute command 6: Register a student for a course.
                if (sChoice.equals("6")) {
                    System.out.print("\nEnter student ID and press return >> ");
                    String sSID = objReader.readLine().trim();
                    System.out.print("\nEnter course ID and press return >> ");
                    String sCID = objReader.readLine().trim();
                    System.out.print("\nEnter course section and press return >> ");
                    String sSection = objReader.readLine().trim();

                    result = registerStudent.execute(sSID + " " + sCID + " " + sSection);
                    System.out.println("\n" + result);
                    log(logWriter, dateFormat, "Register Student (" + sSID + " " + sCID + " " + sSection + "):\n" + result);
                    continue;
                }

                // Terminate this client.
                if (sChoice.equalsIgnoreCase("X")) {
                    log(logWriter, dateFormat, "Client exited.");
                    break;
                }
            }

            // Clean up resources.
            objReader.close();
            logWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Write a log entry with timestamp to the log file.
     */
    private static void log(PrintWriter writer, SimpleDateFormat dateFormat, String message) {
        String timestamp = dateFormat.format(new Date());
        writer.println("[" + timestamp + "] " + message);
        writer.flush();
    }
}
