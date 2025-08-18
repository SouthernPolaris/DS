import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * This interface is for a remote calculator
 */
public interface Calculator extends Remote {

    // Pushes a value onto the stack
    public void pushValue(UUID clientID, int val) throws RemoteException;
    // Pushes an operation onto the stack and performs the operation on all elements in the stack
    public void pushOperation(UUID clientID, String operator) throws RemoteException;

    // Pops value from top of stack
    public int pop(UUID clientID) throws RemoteException;
    
    // Checks if a client stack is empty
    public boolean isEmpty(UUID clientID) throws RemoteException;
    
    // Delays the pop operation
    public int delayPop(UUID clientID, int millis) throws RemoteException;

    // Debug function to print a stack for a client
    public void printStack(UUID clientID) throws RemoteException;
}
