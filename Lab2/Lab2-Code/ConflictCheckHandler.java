import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

/**
 * Course conflict checking component. Subscribes to EV_CHECK_CONFLICT events,
 * validates the registration request, and either announces an error via EV_SHOW
 * or forwards the request to EV_REGISTER_STUDENT if no conflict is found.
 */
@SuppressWarnings("deprecation")
public class ConflictCheckHandler implements Observer {

    private DataBase objDataBase;

    /**
     * Constructs a conflict check handler. Subscribes to EV_CHECK_CONFLICT events at creation.
     *
     * @param objDataBase reference to the database object
     */
    public ConflictCheckHandler(DataBase objDataBase) {
        this.objDataBase = objDataBase;
        EventBus.subscribeTo(EventBus.EV_CHECK_CONFLICT, this);
    }

    /**
     * Event handler. On receiving a check conflict event, validates the registration request and checks for schedule conflicts.
     *
     * @param event an event object. (caution: not to be directly referenced)
     * @param param a parameter object of the event. (to be cast to appropriate data type)
     */
    public void update(Observable event, Object param) {
        StringTokenizer objTokenizer = new StringTokenizer((String) param);
        String sSID = objTokenizer.nextToken();
        String sCID = objTokenizer.nextToken();
        String sSection = objTokenizer.nextToken();
        Student objStudent = this.objDataBase.getStudentRecord(sSID);
        Course objCourse = this.objDataBase.getCourseRecord(sCID, sSection);
        if (objStudent == null) {
            EventBus.announce(EventBus.EV_SHOW, "Invalid student ID");
            return;
        }
        if (objCourse == null) {
            EventBus.announce(EventBus.EV_SHOW, "Invalid course ID or course section");
            return;
        }

        ArrayList vCourse = objStudent.getRegisteredCourses();
        for (int i = 0; i < vCourse.size(); i++) {
            if (((Course) vCourse.get(i)).conflicts(objCourse)) {
                EventBus.announce(EventBus.EV_SHOW, "Registration conflicts");
                return;
            }
        }

        EventBus.announce(EventBus.EV_REGISTER_STUDENT, (String) param);
    }
}
