import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;

/*
 * Tests for single client uses
 */
public class TestCalcClient {
    private static Calculator stub;

    /*
     * Creates a server
     */
    @BeforeAll
    static void createServer() throws Exception {
        // Create registry on port 1099 (arbitrary)
        Registry registry = LocateRegistry.createRegistry(1099);
        Calculator distributedCalc = new CalculatorImplementation();

        registry.rebind("CalculatorDistributed", distributedCalc);
        stub = (Calculator) registry.lookup("CalculatorDistributed");

    }

    /*
     * Test for push/pop for the client
     */
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

    /*
     * Test for running operations (min/max/lcm/gcd)
     */
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

    // Tests for delay pops by timing the difference between the pop start and end
    @Test
    void singleClientDelayPop() throws RemoteException {
        UUID clientID = UUID.randomUUID();
        int mills = 500;
        stub.pushValue(clientID, 10);
        long start = System.currentTimeMillis();
        int popped =  stub.delayPop(clientID, mills);
        long end = System.currentTimeMillis();

        assertEquals(10, popped);
        // Compare greater than or equal-to, accounting for extra execution time
        assertTrue((end - start) >= 500, "DelayPop Should Be Longer Than " + mills + " Milliseconds");
    }

    /*
     * Check for proper throwing when popping from empty stack
     */
    @Test
    void emptyPop() throws RemoteException {
        UUID clientID = UUID.randomUUID();

        assertTrue(stub.isEmpty(clientID));

        RemoteException thrownException = assertThrows(RemoteException.class, () -> {
            stub.pop(clientID);
        });

        assertTrue(thrownException.getMessage().contains("empty stack"));
    }
}
