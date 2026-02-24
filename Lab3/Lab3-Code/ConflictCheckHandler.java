import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ConflictCheckHandler {

    private DBInterface objDataBase;

    public ConflictCheckHandler(DBInterface objDataBase) {
        this.objDataBase = objDataBase;
    }

    public String check(String param) throws RemoteException {
        StringTokenizer objTokenizer = new StringTokenizer(param);
        String sSID = objTokenizer.nextToken();
        String sCID = objTokenizer.nextToken();
        String sSection = objTokenizer.nextToken();

        Student objStudent = this.objDataBase.getStudentRecord(sSID);
        Course objCourse = this.objDataBase.getCourseRecord(sCID, sSection);

        if (objStudent == null) {
            return "Invalid student ID";
        }
        if (objCourse == null) {
            return "Invalid course ID or course section";
        }

        ArrayList vCourse = objStudent.getRegisteredCourses();
        for (int i = 0; i < vCourse.size(); i++) {
            if (((Course) vCourse.get(i)).conflicts(objCourse)) {
                return "Registration conflicts";
            }
        }

        // No conflict.
        return null;
    }
}