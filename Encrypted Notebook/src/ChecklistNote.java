import java.util.*;

// A note made of checklist items, rendered/parsed in a simple Markdown-like format
public class ChecklistNote extends Note {
    ArrayList<ChecklistItem> items;

    // Creates a checklist note from a list of items and tags (uses empty list if null)
    public ChecklistNote(ArrayList<ChecklistItem> items, Set<String> tags) {
        super(tags);
        this.items = items != null ? items : new ArrayList<>();
    }

    @Override
    public String getBodyAsText() {
        // Convert the internal items into a text form like:
        // [x] done item
        // [ ] pending item
        StringBuilder sb = new StringBuilder();
        for (ChecklistItem ci : items)
            sb.append(ci.checked ? "[x] " : "[ ] ").append(ci.text).append("\n");
        return sb.toString();
    }

    public static ChecklistNote fromMarkdown(String md, Set<String> tags) {
        // Parses the same text format produced by getBodyAsText() into items
        ArrayList<ChecklistItem> items = new ArrayList<>();
        if (md == null || md.isEmpty()) return new ChecklistNote(items, tags);
        for (String line : md.split("\n")) {
            String tline = line.trim();
            if (tline.startsWith("[x]"))
                items.add(new ChecklistItem(tline.substring(3).trim(), true));
            else if (tline.startsWith("[ ]"))
                items.add(new ChecklistItem(tline.substring(3).trim(), false));
            else if (!tline.isEmpty())
                items.add(new ChecklistItem(tline, false));
        }
        return new ChecklistNote(items, tags);
    }

    @Override
    public String serialize() {
        // Prefix identifies the type (CHECK), followed by tags on the first line.
        // Subsequent lines list items with [x]/[ ] markers. We strip the record separator
        // char from text to keep the overall file splitting reliable.
        StringBuilder sb = new StringBuilder();
        sb.append("CHECK:").append(String.join(",", tags)).append("\n");
        for (ChecklistItem ci : items)
            sb.append(ci.checked ? "[x] " : "[ ] ").append(ci.text.replace("\u001E", "")).append("\n");
        return sb.toString();
    }

    public static ChecklistNote deserialize(String s) {
        // Expect shape: "CHECK:tag1,tag2\n[x] item one\n[ ] item two..."
        String[] arr = s.split("\n",2);
        String tags = arr[0].substring(6);
        ArrayList<ChecklistItem> items = new ArrayList<>();
        if (arr.length>1)
            for (String line : arr[1].split("\n")) {
                String tline = line.trim();
                if (tline.startsWith("[x]"))
                    items.add(new ChecklistItem(tline.substring(3).trim(), true));
                else if (tline.startsWith("[ ]"))
                    items.add(new ChecklistItem(tline.substring(3).trim(), false));
                else if (!tline.isEmpty())
                    items.add(new ChecklistItem(tline, false));
            }
        return new ChecklistNote(items, parseTags(tags));
    }
}

// Internal data holder for a single checklist row
class ChecklistItem {
    String text;
    boolean checked;
    // Simple value holder for one checklist row-the label and whether it's checked
    ChecklistItem(String text, boolean checked) {
        this.text = text;
        this.checked = checked;
    }
}