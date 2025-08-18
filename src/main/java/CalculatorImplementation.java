import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class CalculatorImplementation extends UnicastRemoteObject implements Calculator {

    /*
     * ----------BONUS MARK-----------
     * Maps a client ID to a specific stack
     */
    private final Map<UUID, Stack<Integer>> clientStacks = new HashMap<UUID, Stack<Integer>>();

    /*
     * Returns the stack of a client
     * Creates a stack if it doesn't exist
     */
    private Stack<Integer> getStack(UUID clientId) {
        return clientStacks.computeIfAbsent(clientId, id -> new Stack<>());
    }

    public CalculatorImplementation() throws RemoteException {
        super();
    }

    /*
     * Pushes a value onto the client's stack
     */
    public void pushValue(UUID clientID, int value) throws RemoteException {
        Stack<Integer> stack = getStack(clientID);
        stack.push(value);
        System.out.println("[SERVER] - Client " + clientID + " || pushed: " + value);
    }

    /*
     * Performs an operation (min/max/lcm/gcd) for a client's stack
     */
    public void pushOperation(UUID clientID, String operation) throws RemoteException {
        Stack<Integer> stack = getStack(clientID);
        int res = 0;

        switch (operation) {
            case "min":
                res = Integer.MAX_VALUE;

                while (!stack.empty()) {
                    res = Math.min(res, stack.pop());
                }

                break;
            case "max":
                res = Integer.MIN_VALUE;

                while (!stack.empty()) {
                    res = Math.max(res, stack.pop());
                }

                break;
            case "gcd":
                if (!stack.empty()) {
                    res = stack.pop();

                    while (!stack.empty()) {
                        res = gcd(stack.pop(), res);
                    }
                }
                break;
            case "lcm":
                if (!stack.empty()) {
                    res = stack.pop();
                    while (!stack.empty()) {
                        int temp = stack.pop();
                        res = Math.abs(temp * res) / gcd(temp, res);
                    }
                }
                break;
        }

        stack.push(res);

        System.out.println("[SERVER] - Client " + clientID + " || " + operation + " || Res: " + res);
    }

    /*
     * Pops an item from a client stack
     * Throws error if no item is present
     */
    public int pop(UUID clientID) throws RemoteException {
        Stack<Integer> stack = getStack(clientID);

        if (stack.isEmpty()) {
            throw new RemoteException("[SERVER] - Client " + clientID + " || Tried popping from empty stack");
        }

        int toPop = stack.pop();
        System.out.println("[SERVER] - Client " + clientID + " || Popped " + toPop + " from its calculator stack");
        return toPop;
    }

    /*
     * Checks if stack is empty
     */
    public boolean isEmpty(UUID clientID) throws RemoteException {
        Stack<Integer> stack = getStack(clientID);
        return stack.isEmpty();
    }

    /*
     * Delays a pop by putting the current thread of a client to sleep for certain milliseconds
     * Each thread is 1-1 per client so it does not impede on other client's operations
     */
    public int delayPop(UUID clientID, int millis) throws RemoteException {
        try {
            Thread.sleep(millis);
            System.out.println("[SERVER] - Client " + clientID + " || delayPop called || " + Thread.currentThread().getName() + " sleeping for " + millis + " milliseconds");
        }
        catch (InterruptedException e) {
            throw new RemoteException("[SERVER] - Client " + clientID + " || Interruption", e);
        }
        return pop(clientID);
    }

    /*
     * Helper function for calculating gcd
     */
    private int gcd(int a, int b) throws RemoteException {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

    /*
    * Debug function for printing the current stack of a client
    */ 
    public void printStack(UUID clientID) throws RemoteException {
        Stack<Integer> stack = getStack(clientID);
        System.out.println("[SERVER] - Client " + clientID + " || printing stack");

        for (Integer integer : stack) {
            System.out.println(integer);
        }
        System.out.println();
    }

}
