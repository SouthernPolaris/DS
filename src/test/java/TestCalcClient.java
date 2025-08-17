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
        Registry registry = LocateRegistry.createRegistry(1099);
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
    void emptyPop() throws RemoteException {
        UUID clientID = UUID.randomUUID();

        assertTrue(stub.isEmpty(clientID));

        RemoteException thrownException = assertThrows(RemoteException.class, () -> {
            stub.pop(clientID);
        });

        assertTrue(thrownException.getMessage().contains("empty stack"));
    }
}
