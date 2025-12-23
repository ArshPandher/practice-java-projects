import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;

// Main application window. Shows a list of notes on the left and the editor on the right.
// Notes are stored encrypted on disk via NoteManager
public class EncryptedNotebookApp extends JFrame {
    private ArrayList<Note> notes;
    private final String password;
    private final NoteManager noteManager;

    // UI Elements
    private JList<String> noteList;
    private DefaultListModel<String> listModel;
    private JTextArea noteArea;
    private JTextField searchField;
    private JTextField tagFilterField;
    private JCheckBox darkModeToggle;
    private JButton saveBtn, newBtn, delBtn, expBtn, impBtn;
    private JComboBox<String> noteTypeCombo;

    // Constructs the main window, remembers the password, loads notes from disk,
    // builds the UI, and populates the list. Had to learn some new methods for this one
    public EncryptedNotebookApp(String password) {
        this.password = password;
        this.setTitle("Encrypted Notebook");
        this.setSize(860, 520);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        noteManager = new NoteManager(password);
        notes = noteManager.loadNotes();

        buildUI();
        refreshList();
    }

    // Builds all Swing components and wires up their event handlers
    private void buildUI() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        listModel = new DefaultListModel<>();
        noteList = new JList<>(listModel);
        JScrollPane listScroll = new JScrollPane(noteList);

        searchField = new JTextField();
        //every edit triggers list filtering
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { searchAndFilter();}
            public void removeUpdate(DocumentEvent e) { searchAndFilter();}
            public void changedUpdate(DocumentEvent e) {}
        });

        tagFilterField = new JTextField();
        // Separate field dedicated to filtering by tags only
        tagFilterField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { searchAndFilter();}
            public void removeUpdate(DocumentEvent e) { searchAndFilter();}
            public void changedUpdate(DocumentEvent e) {}
        });

        JPanel topLeft = new JPanel(new GridLayout(4,1,2,2));
        topLeft.add(new JLabel("Search:"));
        topLeft.add(searchField);
        topLeft.add(new JLabel("Tag Filter:"));
        topLeft.add(tagFilterField);

        leftPanel.add(topLeft, BorderLayout.NORTH);
        leftPanel.add(listScroll, BorderLayout.CENTER);

        // Note type selection
        String[] types = {"Text", "Checklist"};
        noteTypeCombo = new JComboBox<>(types);

        // Main text area
        noteArea = new JTextArea();
        JScrollPane areaScroll = new JScrollPane(noteArea);

        // Buttons
        saveBtn = new JButton("Save");
        newBtn = new JButton("New");
        delBtn = new JButton("Delete");
        expBtn = new JButton("Export");
        impBtn = new JButton("Import");
        darkModeToggle = new JCheckBox("Dark Mode");

        JPanel btnPanel = new JPanel();
        btnPanel.add(noteTypeCombo);
        btnPanel.add(newBtn);
        btnPanel.add(saveBtn);
        btnPanel.add(delBtn);
        btnPanel.add(expBtn);
        btnPanel.add(impBtn);
        btnPanel.add(darkModeToggle);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, areaScroll);
        splitPane.setDividerLocation(260);

        this.add(splitPane, BorderLayout.CENTER);
        this.add(btnPanel, BorderLayout.SOUTH);

        // Event Listeners
        noteList.addListSelectionListener(e -> loadSelectedNote());
        saveBtn.addActionListener(e -> saveCurrentNote());
        newBtn.addActionListener(e -> newNote());
        delBtn.addActionListener(e -> deleteCurrentNote());
        expBtn.addActionListener(e -> exportNotes());
        impBtn.addActionListener(e -> importNotes());
        darkModeToggle.addActionListener(e -> toggleDarkMode());

        // Keyboard shortcut for save
        noteArea.registerKeyboardAction(e -> saveCurrentNote(),
                KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK),
                JComponent.WHEN_FOCUSED);

        UIManager.put("swing.boldMetal", Boolean.FALSE);
    }

    // Rebuilds the list on the left
    private void refreshList() {
        listModel.clear();
        for (Note n : notes) {
            listModel.addElement(n.getDisplayTitle());
        }
    }

    // Filters the visible list by both the full-text search and the tag filter
    private void searchAndFilter() {
        String q = searchField.getText().toLowerCase();
        String tag = tagFilterField.getText().toLowerCase();

        listModel.clear();
        for (Note n : notes) {
            boolean matchesSearch = n.matches(q);
            boolean matchesTag = tag.isEmpty() || n.hasTag(tag);
            if (matchesSearch && matchesTag)
                listModel.addElement(n.getDisplayTitle());
        }
    }

    // Finds the Note object matching the selected title in the list
    // We compare by display title. If titles repeat, the first match wins
    private Note getSelectedNote() {
        int idx = noteList.getSelectedIndex();
        if (idx >= 0) {
            String wantedTitle = listModel.getElementAt(idx);
            for (Note n : notes)
                if (n.getDisplayTitle().equals(wantedTitle))
                    return n;
        }
        return null;
    }

    // Loads the selected note into the editor and sets the type selector accordingly. Had to use AI tools for this section too.
    private void loadSelectedNote() {
        Note note = getSelectedNote();
        if (note != null) {
            noteArea.setText(note.getBodyAsText());
            if (note instanceof ChecklistNote) {
                noteTypeCombo.setSelectedItem("Checklist");
            } else {
                noteTypeCombo.setSelectedItem("Text");
            }
        } else {
            noteArea.setText("");
        }
    }

    // Takes the text from the editor and replaces the selected note with a new
    // instance of the chosen type, reusing the original note's tags
    private void saveCurrentNote() {
        Note selected = getSelectedNote();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a note or create a new one.", "No Note", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String content = noteArea.getText();
        String type = (String) noteTypeCombo.getSelectedItem();
        Note newNote;
        if ("Text".equals(type)) {
            newNote = new TextNote(content, selected.getTags());
        } else {
            newNote = ChecklistNote.fromMarkdown(content, selected.getTags());
        }
        // replace the selected note with the new one
        int idx = notes.indexOf(selected);
        if (idx >= 0) {
            notes.set(idx, newNote);
        } else {
            notes.add(newNote);
        }
        noteManager.saveNotes(notes);
        refreshList();
        selectNoteInList(newNote);
    }

    // Creates a fresh note of the selected type and asks the user for tags
    private void newNote() {
        String type = (String) noteTypeCombo.getSelectedItem();
        String tags = JOptionPane.showInputDialog(this, "Enter tags for the new note (comma separated):", "");
        Set<String> tagset = Note.parseTags(tags);
        Note note;
        if ("Text".equals(type)) {
            note = new TextNote("", tagset);
        } else {
            note = new ChecklistNote(new ArrayList<>(), tagset);
        }
        notes.add(note);
        noteManager.saveNotes(notes);
        refreshList();
        selectNoteInList(note);
        noteArea.setText("");
    }

    // Highlights the given note in the list by matching its display title
    private void selectNoteInList(Note note) {
        int idx = -1;
        String title = note.getDisplayTitle();
        for (int i = 0; i < listModel.size(); ++i)
            if (listModel.getElementAt(i).equals(title))
                idx = i;
        if (idx >= 0) noteList.setSelectedIndex(idx);
    }

    // Confirms with the user then deletes the currently selected note. Took me a lot of time to figure this one out
    private void deleteCurrentNote() {
        Note note = getSelectedNote();
        if (note != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "Really delete this note?", "Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                notes.remove(note);
                noteManager.saveNotes(notes);
                refreshList();
                noteArea.setText("");
            }
        }
    }

    // Saves a plain-text copy of all notes (not encrypted) to a chosen file
    private void exportNotes() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            noteManager.exportNotes(notes, fc.getSelectedFile().getAbsolutePath());
        }
    }

    // Loads notes from a plain-text file and replaces current notes with them
    private void importNotes() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            ArrayList<Note> imported = noteManager.importNotes(fc.getSelectedFile().getAbsolutePath());
            if (imported != null && !imported.isEmpty()) {
                notes = imported;
                noteManager.saveNotes(notes);
                refreshList();
                noteArea.setText("");
            }
        }
    }

    // Applies a simple dark/light theme by switching foreground/background colors. I did this just for practice
    private void toggleDarkMode() {
        boolean dark = darkModeToggle.isSelected();
        Color bg = dark ? new Color(38, 38, 38) : Color.WHITE;
        Color fg = dark ? new Color(220, 220, 220) : Color.BLACK;
        noteArea.setBackground(bg);
        noteArea.setForeground(fg);
        noteList.setBackground(bg);
        noteList.setForeground(fg);
        searchField.setBackground(bg);
        searchField.setForeground(fg);
        tagFilterField.setBackground(bg);
        tagFilterField.setForeground(fg);
    }
}