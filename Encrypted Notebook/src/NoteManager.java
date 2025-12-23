import java.util.*;
import java.io.*;
import javax.swing.*;

// Handles loading/saving notes to disk, including encrypting them with the
// user's password. Also supports plain-text import/export.
public class NoteManager {
    static final String SAVE_FILE = "notes.dat";
    private String password;

    // Creates a manager bound to the given password, used for encrypting/decrypting on save/load
    public NoteManager(String password) {
        this.password = password;
    }

    // Reads the encrypted notes file and returns deserialized Note objects.
    public ArrayList<Note> loadNotes() {
        ArrayList<Note> notes = new ArrayList<>();
        try {
            String enc = readFile(SAVE_FILE);
            if (!enc.isEmpty()) {
                String plain = EncryptionUtil.decrypt(enc, password);
                notes = Note.fromSerializedList(plain);
            }
        } catch (Exception e) {
            // File missing/corrupt
            notes = new ArrayList<>();
        }
        return notes;
    }

    // Serializes notes, encrypts them with the user's password, and writes to disk.
    public void saveNotes(ArrayList<Note> notes) {
        String serialized = Note.serializeList(notes);
        try {
            String enc = EncryptionUtil.encrypt(serialized, password);
            writeFile(SAVE_FILE, enc);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Could not save notes: " + e.getMessage());
        }
    }

    // Writes notes as plain, unencrypted text to the chosen path
    // Useful for backups or migration. Warn users this is not encrypted
    public void exportNotes(ArrayList<Note> notes, String path) {
        try {
            PrintWriter out = new PrintWriter(path);
            out.println(Note.serializeList(notes));
            out.close();
            JOptionPane.showMessageDialog(null, "Exported!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Export error: " + e.getMessage());
        }
    }

    // Reads plain, unencrypted notes from a file and returns them as Note objects.
    public ArrayList<Note> importNotes(String path) {
        ArrayList<Note> notes = new ArrayList<>();
        try {
            String plain = readFile(path);
            notes = Note.fromSerializedList(plain);
            JOptionPane.showMessageDialog(null, "Imported!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Import error: " + e.getMessage());
        }
        return notes;
    }

    // Small utility to read the whole file into a string
    static String readFile(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) return "";
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line=br.readLine())!=null)
            sb.append(line).append('\n');
        br.close();
        return sb.toString().trim();
    }

    // Writes the supplied string to a file, overwriting any previous content.
    static void writeFile(String filename, String content) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        bw.write(content);
        bw.close();
    }
}