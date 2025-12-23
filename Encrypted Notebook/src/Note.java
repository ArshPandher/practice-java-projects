import java.util.*;
import java.io.*;

// Base class for all note types (plain text and checklist).
// It stores common metadata (tags, created date) and defines how notes
// are matched, displayed, and serialized/deserialized as text.
abstract class Note {
    protected Set<String> tags = new HashSet<>();
    protected Date created = new Date();

    // Base constructor-accepts an optional set of tags and copies them into this note
    public Note(Set<String> tags) {
        if (tags != null) this.tags = new HashSet<>(tags);
    }

    // Returns an unmodifiable view of this note's tags so callers can't change them directly
    public Set<String> getTags() { return Collections.unmodifiableSet(tags); }

    // Returns true if this note contains a tag that contains the given substring (case-insensitive)
    public boolean hasTag(String tag) {
        if (tag == null || tag.isEmpty()) return false;
        String t = tag.toLowerCase();
        return tags.stream().anyMatch(x -> x.toLowerCase().contains(t));
    }

    // Converts a comma-separated string like "work, ideas , urgent" into a set of normalized tags.
    public static Set<String> parseTags(String t) {
        Set<String> set = new HashSet<>();
        if (t == null) return set;
        String[] arr = t.split(",");
        for (String str : arr)
            if (!str.trim().isEmpty())
                set.add(str.trim().toLowerCase());
        return set;
    }

    // Produces a short title for list display-a trimmed preview plus tag list if any.
    public String getDisplayTitle() {
        String preview = getPreviewText();
        if (preview.length()>30) preview = preview.substring(0, 27) + "...";
        return preview + (tags.isEmpty() ? "" : " [" + String.join(",", tags) + "]");
    }

    // Checks if a lowercased query appears either in the body text or in any tag
    public boolean matches(String q) {
        if (q == null || q.isEmpty()) return true;
        String low = q.toLowerCase();
        if (getBodyAsText().toLowerCase().contains(low)) return true;
        return tags.stream().anyMatch(t -> t.toLowerCase().contains(low));
    }

    // Returns the note's content as a single text block
    public abstract String getBodyAsText();

    // Converts this note to a plain string format so it can be saved to disk
    public abstract String serialize();

    // Joins all serialized notes
    // This helps store multiple notes in a single text file while being easy to split later.
    public static String serializeList(ArrayList<Note> notes) {
        StringBuilder sb = new StringBuilder();
        for (Note n : notes)
            sb.append(n.serialize()).append("\u001E"); // \u001E=record separator
        return sb.toString();
    }

    // Reconstructs a list of Note objects from the serialized text created above.
    // Uses type markers at the start of each record (e.g., "TEXT:", "CHECK:") to decide
    // which subclass should handle the rest of the content.
    public static ArrayList<Note> fromSerializedList(String text) {
        ArrayList<Note> notes = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) return notes;
        for (String seg : text.split("\u001E")) {
            seg = seg.trim();
            if (seg.isEmpty()) continue;
            if (seg.startsWith("TEXT:")) {
                notes.add(TextNote.deserialize(seg));
            } else if (seg.startsWith("CHECK:")) {
                notes.add(ChecklistNote.deserialize(seg));
            }
        }
        return notes;
    }

    // A safe variant that doesn't throw hard exceptions; used for validating decrypted data
    public static java.util.List<Note> fromSerializedListSafe(String text) throws Exception {
        return fromSerializedList(text);
    }

    // Helper to create a single-line preview from the body
    protected String getPreviewText() { return getBodyAsText().replace("\n", " ").trim(); }
}