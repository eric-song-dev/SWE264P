import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface DBInterface extends Remote {

    ArrayList getAllStudentRecords() throws RemoteException;

    ArrayList getAllCourseRecords() throws RemoteException;

    Student getStudentRecord(String sSID) throws RemoteException;

    String getStudentName(String sSID) throws RemoteException;

    Course getCourseRecord(String sCID, String sSection) throws RemoteException;

    String getCourseName(String sCID) throws RemoteException;

    void makeARegistration(String sSID, String sCID, String sSection) throws RemoteException;
}
