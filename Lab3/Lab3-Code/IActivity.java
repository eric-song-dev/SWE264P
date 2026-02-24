import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IActivity extends Remote {

    /**
     * Execute the handler's logic with the given parameter string.
     *
     * @param param a string parameter for the command
     * @return a string result of command processing
     * @throws RemoteException if a remote communication error occurs
     */
    String execute(String param) throws RemoteException;
}
