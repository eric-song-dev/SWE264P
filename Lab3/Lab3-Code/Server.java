import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    public static void main(String[] args) {
        try {
            // Lookup the DBInterface from the Database registry on port 1099.
            Registry dbRegistry = LocateRegistry.getRegistry("localhost", 1099);
            DBInterface db = (DBInterface) dbRegistry.lookup("DBServer");
            System.out.println("Connected to Database server.");

            // Create conflict check and overbooking components.
            ConflictCheckHandler conflictChecker = new ConflictCheckHandler(db);
            OverbookingHandler overbookingChecker = new OverbookingHandler(db);

            // Create all handler objects.
            ListAllStudentsHandler listAllStudents = new ListAllStudentsHandler(db);
            ListAllCoursesHandler listAllCourses = new ListAllCoursesHandler(db);
            ListStudentsRegisteredHandler listStudentsRegistered = new ListStudentsRegisteredHandler(db);
            ListCoursesRegisteredHandler listCoursesRegistered = new ListCoursesRegisteredHandler(db);
            ListCoursesCompletedHandler listCoursesCompleted = new ListCoursesCompletedHandler(db);
            RegisterStudentHandler registerStudent = new RegisterStudentHandler(db, conflictChecker,
                    overbookingChecker);

            // Create a second RMI registry on port 1100 for handler objects.
            Registry handlerRegistry = LocateRegistry.createRegistry(1100);

            // Bind all handler objects to the handler registry.
            handlerRegistry.rebind("ListAllStudents", listAllStudents);
            handlerRegistry.rebind("ListAllCourses", listAllCourses);
            handlerRegistry.rebind("ListStudentsRegistered", listStudentsRegistered);
            handlerRegistry.rebind("ListCoursesRegistered", listCoursesRegistered);
            handlerRegistry.rebind("ListCoursesCompleted", listCoursesCompleted);
            handlerRegistry.rebind("RegisterStudent", registerStudent);

            System.out.println("Server is running on port 1100...");
            System.out.println("All handlers are bound and ready.");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
