public class Borrower {
    public int id;
    public String name;
    public String borrowedBookId; 

    public Borrower(int id, String name, String borrowedBookId) {
        this.id = id;
        this.name = name;
        this.borrowedBookId = borrowedBookId;
    }

    public String toFileString() {
        return id + ", " + name + ", " + borrowedBookId;
    }
}