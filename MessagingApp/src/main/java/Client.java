import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    BufferedReader incoming;
    PrintWriter outgoing;
    JFrame app = new JFrame("Jared's Messaging App");
    JTextField chatBox = new JTextField(40);
    JTextArea chatWindow = new JTextArea(8, 40);

    public Client() {
        // Send text to server; clear text box.
        chatBox.addActionListener(e -> {
            outgoing.println(chatBox.getText());
            chatBox.setText("");
        });

        // Setup the GUI
        chatWindow.setEditable(false);
        chatBox.setEditable(false);
        app.getContentPane().add(new JScrollPane(chatWindow), "Center");
        app.getContentPane().add(chatBox, "South");
        app.pack();
    }

    public static void main(String[] args) throws Exception {
        Client c = new Client();
        c.app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        c.app.setVisible(true);
        c.run();
    }

    private void run() throws IOException {
        String serverIP = "127.0.0.1"; // Hardcoded because why not.
        Socket s = new Socket(serverIP, 9501);
        // Setup communication streams.
        incoming = new BufferedReader(new InputStreamReader(s.getInputStream()));
        outgoing = new PrintWriter(s.getOutputStream(), true);

        // Process messages from server, according to the protocol.
        while (true) {
            protocol p = protocol.valueOf(incoming.readLine());
            switch (p) {
                case SubmitUser:
                    outgoing.println(getName());
                    break;
                case UserAccepted:
                    chatBox.setEditable(true);
                    break;
                case Message:
                    chatWindow.append(incoming.readLine() + "\n");
                    break;
            }
        }
    }

    private String getName() {
        return JOptionPane.showInputDialog(app, "Login or create a user:", "User Login/Creation Prompt", JOptionPane.PLAIN_MESSAGE);
    } // Null works just fine for the name right now, so I can use that for anonymous until someone logs incoming?
}
