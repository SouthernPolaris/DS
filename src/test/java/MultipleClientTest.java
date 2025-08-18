import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;

public class MultipleClientTest {
    private static Calculator stub;

    // Initialise 4 clients (to test for more than 3 clients)
    private static final UUID client_A_ID = UUID.randomUUID();
    private static final UUID client_B_ID = UUID.randomUUID();
    private static final UUID client_C_ID = UUID.randomUUID();
    private static final UUID client_D_ID = UUID.randomUUID();

    /*
     * Helper function for concurrency testing and error handling
     */
    private Runnable createThread(UUID clientID, int expectedVal, int delayMS, Throwable[] multithreadExceptions, int idx) {
        return () -> {
            try {
                int res = stub.delayPop(clientID, delayMS);
                assertEquals(expectedVal, res);
            } catch (Throwable e) {
                multithreadExceptions[idx] = e;
            }
        };
    }

    // Clear a stack for a client
    private void clearClientStack(UUID clientID) throws RemoteException {
        if (!stub.isEmpty(clientID)) {        
            stub.pushOperation(clientID, "min");
            stub.pop(clientID);
        }
    }

    // Clears all stacks, runs before each Test
    private void clearAllStacks() throws RemoteException {
        clearClientStack(client_A_ID);
        clearClientStack(client_B_ID);
        clearClientStack(client_C_ID);
        clearClientStack(client_D_ID);
    }

    /*
     * Create server before each test
     */
    @BeforeAll
    static void createServer() throws Exception {
        Registry registry = LocateRegistry.createRegistry(8080);
        Calculator distributedCalc = new CalculatorImplementation();

        registry.rebind("MultCalc", distributedCalc);
        stub = (Calculator) registry.lookup("MultCalc");

    }

    /*
    * Tests for independence of multiple stacks
    */ 
    @Test
    void multipleClientsIndependence() throws RemoteException {
        clearAllStacks();

        stub.pushValue(client_A_ID, 10);
        stub.pushValue(client_B_ID, 20);

        stub.pushValue(client_A_ID, 30);

        stub.pushValue(client_C_ID, -19);
        stub.pushValue(client_D_ID, 0);

        assertEquals(30, stub.pop(client_A_ID));
        assertEquals(20, stub.pop(client_B_ID));
        assertEquals(10, stub.pop(client_A_ID));
        assertEquals(-19, stub.pop(client_C_ID));
        assertEquals(0, stub.pop(client_D_ID));
    }

    /*
     * Tests for push/pop for multiple clients
     */
    @Test
    void MultipleClientsPushPop() throws RemoteException {
        clearAllStacks();

        stub.pushValue(client_A_ID, 10);
        stub.pushValue(client_B_ID, 20);

        stub.pushValue(client_A_ID, 30);
        stub.pushValue(client_B_ID, 0);

        stub.pushValue(client_C_ID, -2478824);
        stub.pushValue(client_D_ID, 32804);

        assertFalse(stub.isEmpty(client_A_ID));

        stub.pushOperation(client_A_ID, "min");
        stub.pushOperation(client_B_ID, "max");

        assertEquals(10, stub.pop(client_A_ID));
        assertEquals(20, stub.pop(client_B_ID));
        assertEquals(-2478824, stub.pop(client_C_ID));
        assertEquals(32804, stub.pop(client_D_ID));
        
        // Check if empty after pop operation
        assertTrue(stub.isEmpty(client_A_ID));
        assertTrue(stub.isEmpty(client_B_ID));
        assertTrue(stub.isEmpty(client_C_ID));
        assertTrue(stub.isEmpty(client_D_ID));
        
    }

    @Test
    void multipleClientsOperations() throws RemoteException {
        clearAllStacks();

        stub.pushValue(client_A_ID, 10);
        stub.pushValue(client_B_ID, 20);
        stub.pushValue(client_B_ID, 18);
        stub.pushValue(client_A_ID, 30);
        stub.pushValue(client_C_ID, 0);
        stub.pushValue(client_D_ID, -10);
        stub.pushValue(client_D_ID, 0);
        stub.pushValue(client_D_ID, 10);

        stub.pushOperation(client_A_ID, "lcm");
        stub.pushOperation(client_B_ID, "gcd");
        stub.pushOperation(client_C_ID, "min");
        stub.pushOperation(client_D_ID, "max");

        assertEquals(30, stub.pop(client_A_ID));
        assertEquals(2, stub.pop(client_B_ID));
        assertEquals(0, stub.pop(client_C_ID));
        assertEquals(10, stub.pop(client_D_ID));
    }

    /*
     * Test for delay pop
     * Uses multithreading to test for concurrency as well
     */
    @Test
    void MultipleClientsDelayPop() throws RemoteException, InterruptedException {
        clearAllStacks();

        stub.pushValue(client_A_ID, 10);
        stub.pushValue(client_B_ID, 20);
        stub.pushValue(client_C_ID, 0);
        stub.pushValue(client_D_ID, -199);

        Throwable[] multithreadExceptions = new Throwable[4];

        Thread thr1 = new Thread(createThread(client_A_ID, 10, 500, multithreadExceptions, 0));
        Thread thr2 = new Thread(createThread(client_B_ID, 20, 1000, multithreadExceptions, 1));
        Thread thr3 = new Thread(createThread(client_C_ID, 0, 200, multithreadExceptions, 2));
        Thread thr4 = new Thread(createThread(client_D_ID, -199, 2000, multithreadExceptions, 3));

        thr1.start();
        thr2.start();
        thr3.start();
        thr4.start();
        
        thr1.join();
        thr2.join();
        thr3.join();
        thr4.join();

        // Tests for throwables to fail tests as using assert throwable overrides assertEqual checks
        for (Throwable e : multithreadExceptions) {
            if (e != null) {
                fail();
            }
        }
    }
}
