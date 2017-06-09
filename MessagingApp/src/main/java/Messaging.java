import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by jaredweakly on 6/8/17.
 */ // Each instance handles a single user and all of their interactions with the server.
class Messaging extends Thread {
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

            // Request a unique username from the client.
            while (true) {
                outgoing.println(protocol.SubmitUser);
                user = incoming.readLine();
                User u = new User(outgoing, user);
                if (user == null) return;

                // Add user to list of all users.
                // Required because of threaded stuff being async.
                synchronized (Server.userList) {
                    if (!Server.userList.contains(u)) {
                        Server.userList.add(u);
                        outgoing.println(protocol.UserAccepted);
                        if (Server.history.containsKey(u)) {
                            for (String line : Server.history.get(u)) {
                                u.getVoice().println(protocol.Login);
                                u.getVoice().println(line);
                            }
                        }
                        break;
                    }
                }
            }

            // Broadcast new user joining.
            announceConnection("Joined");

            // Accept messages from this client and broadcast them.
            while (true) {
                // Instead of just accepting lines, process lines.
                String input = incoming.readLine();
                if (input == null) return;
                    switch (input) {
                        case ".names":
                            outgoing.println(protocol.Command);
                            outgoing.println(Server.history.keySet().toString());
                            break;
                        case ".quit":
//                            quit(user);
                            outgoing.println(protocol.Quit);
                            break;
                        case ".query":
                            String[] str = incoming.readLine().split(" ", 2);
                            broadcastToUser(str[0],str[1]);
                            break;
                        default:
                            broadcast(user + ": " + input);
                            break;
                    }
//                }
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            quit(user);
        }
    }

    private void quit(String uname) {
        announceConnection("Exited");
        for (User u : Server.userList)
            if (u.name.equals(uname))
                Server.userList.remove(u);
    }

    private void quit() {
        // Cleanup code
        announceConnection("Exited");
        for (User u : Server.userList) {
            Server.userList.remove(u);
        }
        try {
            s.close();
        } catch (IOException e) {}
    }

    // todo: slim this function down. It's way too ugly.
    public void broadcastToUser(String n, String query) {
        User tmp = null;
        for (User them : Server.userList) {
            if (them.name.equals(n)) {
                tmp = them;
                break;
            }
        }

        tmp.history.add(user + ": " + query);
        Server.history.put(tmp, tmp.history);
        tmp.getVoice().println(protocol.Message);
        tmp.getVoice().println(user + ": " + query);

        for (User me : Server.userList) {
            if (me.name.equals(user)) {
                tmp = me;
                break;
            }
        }

        tmp.history.add(user + ": " + query);
        Server.history.put(tmp,tmp.history);
        tmp.getVoice().println(protocol.Message);
        tmp.getVoice().println(user + ": " + query);
        return;
    }

    public void announceConnection(String type) {
        String newUser = user;
        broadcast(newUser + " has " + type + " the chatroom.");
    }

    public void broadcast(String s) {
        for (User u : Server.userList) {
            u.history.add(s);
            Server.history.put(u,u.history);
            u.getVoice().println(protocol.Message);
            u.getVoice().println(s);
        }
    }
}
