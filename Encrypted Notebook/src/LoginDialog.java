import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.List;

// Simple blocking dialog that asks for the master password. It supports three cases:
// 1) Normal login when a password hash exists.
// 2) First run with no password file (creates one).
// 3) Existing notes but no password file (attempts to decrypt notes to validate password, then saves hash).
public class LoginDialog extends JDialog {
    private JPasswordField passField;
    private boolean succeeded = false;
    private String password = "";
    private static final String PASSWORD_FILE = ".notebookpw";
    private static final String NOTES_FILE = NoteManager.SAVE_FILE;

    // Creates and shows a modal dialog prompting the user for the master password
    public LoginDialog(Frame parent) {
        super(parent, "Notebook Login", true);
        JPanel panel = new JPanel(new BorderLayout(5,5));
        panel.add(new JLabel("Master Password:"), BorderLayout.WEST);
        passField = new JPasswordField(18);
        panel.add(passField, BorderLayout.CENTER);
        JButton ok = new JButton("Login");
        ok.addActionListener(e -> handleLogin());
        this.add(panel, BorderLayout.CENTER);
        this.add(ok, BorderLayout.SOUTH);
        this.pack();
        setLocationRelativeTo(parent);
        this.setVisible(true);
    }

    // Validates the entered password against stored hash if available.
    // If no hash exists, decides whether to create a new password file
    // or validate against existing encrypted notes.
    private void handleLogin() {
        password = new String(passField.getPassword()).trim();
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            File pwFile = new File(PASSWORD_FILE);
            File notesFile = new File(NOTES_FILE);

            if (pwFile.exists()) {
                // Normal case: verify stored hash
                String storedHash = new String(Files.readAllBytes(pwFile.toPath()), "UTF-8").trim();
                if (storedHash.equals(hash(password))) {
                    succeeded = true;
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Wrong password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // No password file yet
                if (notesFile.exists()) {
                    // There are existing encrypted notes: try to decrypt using entered password
                    try {
                        String enc = NoteManager.readFile(NOTES_FILE);
                        if (enc.isEmpty()) {
                            // No content: treat as fresh
                            Files.write(pwFile.toPath(), hash(password).getBytes("UTF-8"));
                            succeeded = true;
                            this.dispose();
                            return;
                        }
                        String plain = EncryptionUtil.decrypt(enc, password);
                        // Try parsing to ensure valid format
                        List<Note> notes = Note.fromSerializedListSafe(plain);
                        // If successful, save the hash and proceed
                        Files.write(pwFile.toPath(), hash(password).getBytes("UTF-8"));
                        succeeded = true;
                        this.dispose();
                    } catch (Exception dex) {
                        JOptionPane.showMessageDialog(this, "Unable to decrypt existing notes with that password.", "Wrong Password", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // No existing notes and no password -> first run, create password file
                    Files.write(pwFile.toPath(), hash(password).getBytes("UTF-8"));
                    succeeded = true;
                    this.dispose();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not read/write password file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Indicates whether the user successfully authenticated and the dialog closed OK
    public boolean isSucceeded() { return succeeded; }
    // Returns the password entered by the user (used to open the notebook)
    public String getPassword() { return password; }

    // Produces a stable hex-encoded SHA-256 of the password for local verification only.
    // This is not used to encrypt the notes themselves (see EncryptionUtil for that). Used AI for this class also as I could not
    // figure out how to produce a stable hex-encoded SHA-256 of the password.
    public static String hash(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes("UTF-8"));
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) hex.append(String.format("%02x", b));
        return hex.toString();
    }
}