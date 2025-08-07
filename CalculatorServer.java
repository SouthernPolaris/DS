import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CalculatorServer {
    public static void main(String[] args) {
        try {
            Calculator myCalc = new CalculatorImplementation();

            Registry registry = LocateRegistry.createRegistry(1099);

            registry.rebind("CalculatorDistributed", myCalc);
            System.out.println("Calculator server ready");
        } catch (Exception e) {
            System.out.println("Error Creating Server");
            e.printStackTrace();
        }
    }
}
