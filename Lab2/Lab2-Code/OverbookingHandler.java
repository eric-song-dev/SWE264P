import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

/**
 * Overbooking notification component that monitors course enrollment.
 * When a course has more than 3 students registered, it announces
 * an overbooking warning via the event bus.
 */
@SuppressWarnings("deprecation")
public class OverbookingHandler implements Observer {

    private static final int OVERBOOK_THRESHOLD = 3;
    private DataBase objDataBase;

    /**
     * Constructs an overbooking handler. Subscribes to EV_REGISTRATION_SUCCESS events at creation.
     *
     * @param objDataBase reference to the database object
     */
    public OverbookingHandler(DataBase objDataBase) {
        this.objDataBase = objDataBase;
        EventBus.subscribeTo(EventBus.EV_REGISTRATION_SUCCESS, this);
    }

    /**
     * Event handler. On receiving a registration success event, checks if the course is overbooked and announces a warning if so.
     *
     * @param event an event object. (caution: not to be directly referenced)
     * @param param a parameter object of the event. (to be cast to appropriate data type)
     */
    public void update(Observable event, Object param) {
        StringTokenizer objTokenizer = new StringTokenizer((String) param);
        String sCID = objTokenizer.nextToken();
        String sSection = objTokenizer.nextToken();

        Course objCourse = objDataBase.getCourseRecord(sCID, sSection);
        if (objCourse != null) {
            ArrayList vStudents = objCourse.getRegisteredStudents();
            int numRegistered = vStudents.size();
            if (numRegistered > OVERBOOK_THRESHOLD) {
                EventBus.announce(EventBus.EV_SHOW,
                        "WARNING: Course " + sCID + " Section " + sSection.toUpperCase()
                                + " is overbooked! (" + numRegistered + " students registered, limit is "
                                + OVERBOOK_THRESHOLD + ")");
            }
        }
    }
}
