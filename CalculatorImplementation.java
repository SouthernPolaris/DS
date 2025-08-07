import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Stack;

public class CalculatorImplementation implements Calculator {

    public Stack<Integer> calcStack = new Stack<>();

    public void pushValue(int value) throws RemoteException {
        calcStack.push(value);
    }

    public void pushOperation(String operation) throws RemoteException {

        int res = 0;

        switch (operation) {
            case "min":
                res = Math.min(res, calcStack.pop());
                break;
            case "max":
                res = Math.max(res, calcStack.pop());
            case "gcd":
                res = gcd(calcStack.pop(), res);
            case "lcm":
                res = 0;
        }

        calcStack.push(res);

    }

    public int pop() throws RemoteException {
        return calcStack.pop();
    }

    public boolean isEmpty() throws RemoteException {
        return calcStack.isEmpty();
    }

    public int delayPop(int millis) throws RemoteException {
        // TODO: add delay
        return calcStack.pop();
    }

    private int gcd(int a, int b) throws RemoteException {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

    public int allGCD() throws RemoteException {
        int res = calcStack.pop();
        while (!calcStack.isEmpty()) {
            res = gcd(res, calcStack.pop());

            if (res == 1) {
                return 1;
            }
        }

        return res;
    }

}
