import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class Server {
    private static final int portNumber = 9501;
    private static HashSet<PrintWriter> userVoice = new HashSet<>();
    private static HashSet<String> usernames = new HashSet<>();

    // Listen on port. Handle spawning.
    public static void main(String[] args) throws Exception {
        System.out.println("Successful.");
        ServerSocket listener = new ServerSocket(portNumber);
        try {
            while (true) {
                new Messaging(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    // Each instance handles a single user and all of their interactions with the server.
    private static class Messaging extends Thread {
        private String user;
        private Socket s;
        private BufferedReader incoming;
        private PrintWriter outgoing;

        // Makes the thread
        public Messaging(Socket s) {
            this.s = s;
        }

        // Request username. Then broadcast all messages.
        public void run() {
            try {
                // Create character streams for the s.
                incoming = new BufferedReader(new InputStreamReader(s.getInputStream()));
                outgoing = new PrintWriter(s.getOutputStream(), true);

                // Request a user from the client. Must be unique.
                while (true) {
                    outgoing.println(protocol.SubmitUser);
                    user = incoming.readLine();
                    if (user == null) return;

                    // Required because of threaded stuff being async.
                    synchronized (usernames) {
                        if (usernames.contains(user) == false) break;
                    }
                }

                // Add user to list of all users.
                // Again, async requirement here.
                synchronized (usernames) {
                    usernames.add(user);
                    userVoice.add(outgoing);
                    outgoing.println(protocol.UserAccepted);
                }

                // Broadcast new user joining.
                announceConnection("Joined");

                // Accept messages from this client and broadcast them.
                while (true) {
                    String input = incoming.readLine();
                    if (input == null) return;

                    broadcast(user + ": " + input);
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                // Cleanup code
                if (user != null) usernames.remove(user);
                if (outgoing != null) userVoice.remove(outgoing);
                try {
                    s.close();
                } catch (IOException e) {
                }
            }
        }

        public void announceConnection(String type) {
            String newUser = user;
            broadcast(newUser + " has " + type + " the chatroom.");
        }

        public void broadcast(String s) {
            for (PrintWriter u : userVoice) {
                u.println(protocol.Message);
                u.println(s);
            }
        }
    }
}
