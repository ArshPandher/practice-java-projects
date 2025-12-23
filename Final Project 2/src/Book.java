public class Book {
    public int id;
    public String title;
    public String author;
    public String isbn;
    public String status;

    public Book(int id, String title, String author, String isbn, String status) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.status = status;
    }

    public String toFileString() {
        return id + ", " + title + ", " + author + ", " + isbn + ", " + status;
    }
}