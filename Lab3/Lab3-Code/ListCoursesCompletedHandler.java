import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ListCoursesCompletedHandler extends UnicastRemoteObject implements IActivity {

    private static final long serialVersionUID = 1L;
    private DBInterface objDataBase;

    public ListCoursesCompletedHandler(DBInterface objDataBase) throws RemoteException {
        super();
        this.objDataBase = objDataBase;
    }

    /**
     * Process "List courses a student has completed" command event.
     *
     * @param param a string parameter for command
     * @return a string result of command processing
     */
    public String execute(String param) throws RemoteException {
        // Parse the parameters.
        StringTokenizer objTokenizer = new StringTokenizer(param);
        String sSID = objTokenizer.nextToken();

        // Get the list of courses the given student has completed.
        Student objStudent = this.objDataBase.getStudentRecord(sSID);
        if (objStudent == null) {
            return "Invalid student ID";
        }
        ArrayList vCourseID = objStudent.getCompletedCourses();

        // Construct a list of course information and return it.
        String sReturn = "";
        for (int i = 0; i < vCourseID.size(); i++) {
            String sCID = (String) vCourseID.get(i);
            String sName = this.objDataBase.getCourseName(sCID);
            sReturn += (i == 0 ? "" : "\n") + sCID + " " + (sName == null ? "Unknown" : sName);
        }
        return sReturn;
    }
}
