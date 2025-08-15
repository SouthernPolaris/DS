import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;

public class TestCalcClient {
    private static Calculator stub;

    @BeforeAll
    static void createServer() throws Exception {
        Registry registry = LocateRegistry.createRegistry(8080);
        Calculator distributedCalc = new CalculatorImplementation();

        registry.rebind("CalculatorDistributed", distributedCalc);
        stub = (Calculator) registry.lookup("CalculatorDistributed");

    }

    @Test
    void singleClientPushPop() throws RemoteException {
        UUID clientID = UUID.randomUUID();
        stub.pushValue(clientID, 10);
        stub.pushValue(clientID, 20);

        int firstPop = stub.pop(clientID);
        int secondPop = stub.pop(clientID);

        assertEquals(20, firstPop);
        assertEquals(10, secondPop);
    }

    @Test
    // Tests for independence of multiple stacks
    void multipleClientsIndependence() throws RemoteException {
        UUID client_A_ID = UUID.randomUUID();
        UUID client_B_ID = UUID.randomUUID();

        stub.pushValue(client_A_ID, 10);
        stub.pushValue(client_B_ID, 20);

        stub.pushValue(client_A_ID, 30);

        assertEquals(30, stub.pop(client_A_ID));
        assertEquals(20, stub.pop(client_B_ID));
        assertEquals(10, stub.pop(client_A_ID));
    }

    @Test
    void MultipleClientsPushPop() throws RemoteException {
        UUID client_A_ID = UUID.randomUUID();
        UUID client_B_ID = UUID.randomUUID();

        stub.pushValue(client_A_ID, 10);
        stub.pushValue(client_B_ID, 20);

        stub.pushValue(client_A_ID, 30);

        stub.pushOperation(client_A_ID, "min");

        assertEquals(10, stub.pop(client_A_ID));
        assertEquals(20, stub.pop(client_B_ID));
        // Check if empty after pop operation
        assertTrue(stub.isEmpty(client_A_ID));
        assertTrue(stub.isEmpty(client_B_ID));
    }

    @Test
    void singleClientOperations() throws RemoteException {
        UUID clientID = UUID.randomUUID();
        stub.pushValue(clientID, 19);
        stub.pushValue(clientID, 10);

        stub.pushOperation(clientID, "lcm");
        assertEquals(190, stub.pop(clientID));

        stub.pushValue(clientID, 20);
        stub.pushValue(clientID, 30);
        stub.pushOperation(clientID, "gcd");
        assertEquals(10, stub.pop(clientID));

        stub.pushValue(clientID, 10);
        stub.pushValue(clientID, -1019);
        stub.pushOperation(clientID, "min");
        assertEquals(-1019, stub.pop(clientID));

        stub.pushValue(clientID, -7);
        stub.pushValue(clientID, -892);
        stub.pushOperation(clientID, "max");
        assertEquals(-7, stub.pop(clientID));
    }

    @Test
    void multipleClientsOperations() throws RemoteException {
        UUID client_A_ID = UUID.randomUUID();
        UUID client_B_ID = UUID.randomUUID();

        stub.pushValue(client_A_ID, 10);
        stub.pushValue(client_B_ID, 20);
        stub.pushValue(client_B_ID, 18);
        stub.pushValue(client_A_ID, 30);

        stub.pushOperation(client_A_ID, "lcm");
        stub.pushOperation(client_B_ID, "gcd");

        assertEquals(30, stub.pop(client_A_ID));
        assertEquals(2, stub.pop(client_B_ID));
    }

    @Test
    void singleClientDelayPop() throws RemoteException {
        UUID clientID = UUID.randomUUID();
        int mills = 500;
        stub.pushValue(clientID, 10);
        long start = System.currentTimeMillis();
        int popped =  stub.delayPop(clientID, mills);
        long end = System.currentTimeMillis();

        assertEquals(10, popped);
        assertTrue((end - start) >= 500, "DelayPop Should Be Longer Than " + mills + " Milliseconds");
    }

    @Test
    void MultipleClientsDelayPop() throws RemoteException, InterruptedException {
        UUID client_A_ID = UUID.randomUUID();
        UUID client_B_ID = UUID.randomUUID();

        stub.pushValue(client_A_ID, 10);
        stub.pushValue(client_B_ID, 20);

        Thread thr1 = new Thread(() -> {
            try {
                assertEquals(10, stub.delayPop(client_A_ID, 500));
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });


        Thread thr2 = new Thread(() -> {
           try {
               assertEquals(20, stub.delayPop(client_B_ID, 1000));
           } catch (RemoteException e) {
               throw new RuntimeException(e);
           }
        });

        thr1.start();
        thr2.start();
        thr1.join();
        thr2.join();
    }
}
