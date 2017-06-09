import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.net.ServerSocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * Created by jaredweakly on 6/8/17.
 */
class TestTest {
    public static final int portNumber = 9001;
    @Test
    @DisplayName("A JUnit 5 test")
    void myTestTest(TestInfo testInfo) throws Exception {
        ServerSocket listener = new ServerSocket(portNumber);
        assertEquals(9001,portNumber);
        try {
            while (true) {
                assertEquals(2,1, "2 is not equal to 1");
                new Messaging(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }
}