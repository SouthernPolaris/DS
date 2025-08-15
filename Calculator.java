import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface Calculator extends Remote {

    public void pushValue(UUID clientID, int val) throws RemoteException;
    public void pushOperation(UUID clientID, String operator) throws RemoteException;

    public int pop(UUID clientID) throws RemoteException;
    public boolean isEmpty(UUID clientID) throws RemoteException;
    public int delayPop(UUID clientID, int millis) throws RemoteException;

    public void printStack(UUID clientID) throws RemoteException;
}
