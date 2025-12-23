import java.util.*;

// A simple free-form text note. Stores a single text body plus inherited tags.
public class TextNote extends Note {
    String body;

    // Creates a text note with the provided body and tags (null body becomes an empty string)
    public TextNote(String body, Set<String> tags) {
        super(tags);
        this.body = body != null ? body : "";
    }

    @Override
    // Returns the raw text body that represents this note's content
    public String getBodyAsText() { return body; }

    @Override
    public String serialize() {
        // Prefix identifies the type (TEXT) followed by comma-separated tags, then a newline,
        // then the body. We strip the record separator char just in case it appears in text.
        return "TEXT:" + String.join(",", tags) + "\n" + body.replace("\u001E", "");
    }

    public static TextNote deserialize(String s) {
        // Expect shape: "TEXT:tag1,tag2\n<body>"
        String[] arr = s.split("\n",2);
        String tags = arr[0].substring(5);
        return new TextNote(arr.length>1 ? arr[1]:"", parseTags(tags));
    }
}