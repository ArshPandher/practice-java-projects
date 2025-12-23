import javax.swing.*;

// Had to take help from AI tools for this class
public class Main {
    // Starts the application on the Swing event thread
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginDialog login = new LoginDialog(null);
            if (login.isSucceeded()) {
                EncryptedNotebookApp app = new EncryptedNotebookApp(login.getPassword());
                app.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}