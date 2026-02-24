import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class OverbookingHandler {

    private static final int OVERBOOK_THRESHOLD = 3;
    private DBInterface objDataBase;

    public OverbookingHandler(DBInterface objDataBase) {
        this.objDataBase = objDataBase;
    }

    public String check(String sCID, String sSection) throws RemoteException {
        Course objCourse = objDataBase.getCourseRecord(sCID, sSection);
        if (objCourse != null) {
            ArrayList vStudents = objCourse.getRegisteredStudents();
            int numRegistered = vStudents.size();
            if (numRegistered > OVERBOOK_THRESHOLD) {
                return "WARNING: Course " + sCID + " Section " + sSection.toUpperCase()
                        + " is overbooked! (" + numRegistered + " students registered, limit is "
                        + OVERBOOK_THRESHOLD + ")";
            }
        }
        return null;
    }
}