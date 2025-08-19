import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.UUID;

/*
 * Implementation of the Client that interacts with the calculator
 */
public class CalculatorClient {

    // UUID for unique identification of a client
    private final UUID clientID = UUID.randomUUID();
    private final Calculator stub;

    // Constructor
    public CalculatorClient(Calculator stub) {
        this.stub = stub;
    }

    // Running client
    public void run() throws RemoteException {
        // Input scanner
        Scanner inptScanner = new Scanner(System.in);
        printHelp();
        while (true) {
            // Get input
            String op = inptScanner.nextLine();

            if (!parseCmd(op)) {
                // Close scanner and quit
                inptScanner.close();
                break;
            }
        }
    }

    private boolean parseCmd(String op) throws RemoteException {
        
        // Split strings by whitespace (1 or more whitespace)
        String[] ops = op.split("\\s+");

        // Switch case for first string in the input
        switch (ops[0]) {
            case "push":
                // Push onto client stack
                int num = Integer.parseInt(ops[1]);
                stub.pushValue(clientID, num);
                System.out.println("[CLIENT] - Pushed value: " + num);
                break;
            case "pop":
                // Basic pop from stack of client
                try {
                    int poppedNum = stub.pop(clientID);
                    System.out.println("[CLIENT] - Popped: " + poppedNum);
                } catch (Exception e) {
                    System.out.println("[CLIENT] - Empty Stack, Cannot Pop");
                }
                break;
            case "delayPop":
                // Convert second word to int and call delayPop
                try {
                    int delay = Integer.parseInt(ops[1]);
                    int delayedPopNum = stub.delayPop(clientID, delay);
                    System.out.println("[CLIENT] - Popped with delay: " + delayedPopNum);
                } catch (Exception e) {
                    System.out.println("[CLIENT] - Empty Stack, Cannot Delay Pop");
                }
                break;
            case "op":
                // Checks for operation type in second word of input
                stub.pushOperation(clientID, ops[1]);
                System.out.println("[CLIENT] - Ran Operation: " + ops[1]);
                break;
            case "help":
                // Prints the help command
                printHelp();
                System.out.println("[CLIENT] - Help");
                break;
            case "debugPrint":
                // Print command for debugging
                stub.printStack(clientID);
                System.out.println("[CLIENT] - Printed Stack on Server");
                break;
            case "exit":
                // Quits client
                System.out.println("[CLIENT] - Exit");
                return false;
            default:
                // Base case if none of above
                System.out.println("[CLIENT] - Unknown Command");
        }
        return true;
    }

    /*
     * Print help text on what to input
     */
    private void printHelp() {
        String actionInfoLine = "\n"+
                "Choose Operation:\n" +
                "push <integer value>\n" +
                "pop\n" +
                "delayPop <milliseconds>\n" +
                "op <min/max/lcm/gcd>\n" +
                "exit\n" +
                "help\n";
        System.out.println(actionInfoLine);
    }

    /*
     * Main function setting up registry and stub
     * Executes the run command
     */
    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry("localhost", 8080);
        Calculator stub = (Calculator) registry.lookup("CalculatorDistributed");

        new CalculatorClient(stub).run();
    }
}