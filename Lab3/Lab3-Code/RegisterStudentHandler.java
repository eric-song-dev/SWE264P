import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.StringTokenizer;

/**
 * "Register a student for a course" command event handler.
 */
public class RegisterStudentHandler extends UnicastRemoteObject implements IActivity {

    private static final long serialVersionUID = 1L;
    private DBInterface objDataBase;
    private ConflictCheckHandler conflictChecker;
    private OverbookingHandler overbookingChecker;

    public RegisterStudentHandler(DBInterface objDataBase,
            ConflictCheckHandler conflictChecker,
            OverbookingHandler overbookingChecker) throws RemoteException {
        super();
        this.objDataBase = objDataBase;
        this.conflictChecker = conflictChecker;
        this.overbookingChecker = overbookingChecker;
    }

    /**
     * Process "Register a student for a course" command event.
     *
     * @param param a string parameter for command
     * @return a string result of command processing
     */
    public String execute(String param) throws RemoteException {
        // Parse the parameters.
        StringTokenizer objTokenizer = new StringTokenizer(param);
        String sSID = objTokenizer.nextToken();
        String sCID = objTokenizer.nextToken();
        String sSection = objTokenizer.nextToken();

        // Check for conflicts using ConflictCheckHandler.
        String conflict = conflictChecker.check(param);
        if (conflict != null) {
            return conflict;
        }

        // No conflict — proceed to register.
        this.objDataBase.makeARegistration(sSID, sCID, sSection);

        // Check for overbooking using OverbookingHandler.
        String warning = overbookingChecker.check(sCID, sSection);
        String result = "Successful!";
        if (warning != null) {
            result += "\n" + warning;
        }

        return result;
    }
}
