import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by jaredweakly on 6/8/17.
 * This is a single user with a merged voice and nickname.
 */
public class User {
    public PrintWriter voice; // This is the "chat" history. I need to store this.
    public String name;
    public ArrayList<String> history = new ArrayList<>();

    public User(PrintWriter voice, String name) {
        this.voice = voice;
        this.name = name;
    }

    public PrintWriter getVoice() {
        return voice;
    }

    public String toString() {
        return name;
    }

    @Override
    public int hashCode() { return name.hashCode(); }

    public boolean equals(Object o) {
        if (!(o instanceof User)) return false;
        User fst = (User) o;
        return fst.name.equals(name);
    }
}
