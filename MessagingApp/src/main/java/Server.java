import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    public static final int portNumber = 10801;
    public static final CopyOnWriteArrayList<User> userList = new CopyOnWriteArrayList<>();
    public static final HashMap<User,ArrayList<String>> history = new HashMap<>();

    // Listen on port. Handle spawning.
    public static void main(String[] args) throws Exception {
        System.out.println("Successful Connection.");
        ServerSocket listener = new ServerSocket(portNumber);
        try {
            while (true) {
                new Messaging(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }
}
