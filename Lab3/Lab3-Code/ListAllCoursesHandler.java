import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ListAllCoursesHandler extends UnicastRemoteObject implements IActivity {

    private static final long serialVersionUID = 1L;
    private DBInterface objDataBase;

    public ListAllCoursesHandler(DBInterface objDataBase) throws RemoteException {
        super();
        this.objDataBase = objDataBase;
    }

    /**
     * Process "List all courses" command event.
     *
     * @param param a string parameter for command
     * @return a string result of command processing
     */
    public String execute(String param) throws RemoteException {
        // Get all course records.
        ArrayList vCourse = this.objDataBase.getAllCourseRecords();

        // Construct a list of course information and return it.
        String sReturn = "";
        for (int i = 0; i < vCourse.size(); i++) {
            sReturn += (i == 0 ? "" : "\n") + ((Course) vCourse.get(i)).toString();
        }
        return sReturn;
    }
}
