import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.UUID;


public class CalculatorClient {

    private final UUID clientID = UUID.randomUUID();
    private final Calculator stub;

    public CalculatorClient(Calculator stub) {
        this.stub = stub;
    }

    public void run() throws RemoteException {
        Scanner inptScanner = new Scanner(System.in);
        printHelp();
        while (true) {
            String op = inptScanner.nextLine();
            if (!parseCmd(op)) {
                break;
            }
        }
    }

    private boolean parseCmd(String op) throws RemoteException {
        String[] ops = op.split("\\s+");

        switch (ops[0]) {
            case "push":
                int num = Integer.parseInt(ops[1]);
                stub.pushValue(clientID, num);
                System.out.println("[CLIENT] - Pushed value: " + num);
                break;
            case "pop":
                int poppedNum = stub.pop(clientID);
                System.out.println("[CLIENT] - Popped: " + poppedNum);
                break;
            case "delayPop":
                int delay = Integer.parseInt(ops[1]);
                int delayedPopNum = stub.delayPop(clientID, delay);
                System.out.println("[CLIENT] - Popped with delay: " + delayedPopNum);
                break;
            case "op":
                stub.pushOperation(clientID, ops[1]);
                System.out.println("[CLIENT] - Ran Operation: " + ops[1]);
                break;
            case "help":
                printHelp();
                System.out.println("[CLIENT] - Help");
                break;
            case "debugPrint":
                stub.printStack(clientID);
                System.out.println("[CLIENT] - Printed Stack on Server");
                break;
            case "exit":
                System.out.println("[CLIENT] - Exit");
                return false;
            default:
                System.out.println("[CLIENT] - Unknown Command");
        }
        return true;
    }

    private void printHelp() {
        String actionInfoLine = """
                Choose Operation:
                push <integer value>
                pop
                delayPop <milliseconds>
                op <min/max/lcm/gcd>
                exit
                help
                """;
        System.out.println(actionInfoLine);
    }

    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry("localhost", 8080);
        Calculator stub = (Calculator) registry.lookup("CalculatorDistributed");

        new CalculatorClient(stub).run();
    }
}