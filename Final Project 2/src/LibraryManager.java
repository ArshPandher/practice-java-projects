import java.util.*;

public class LibraryManager {
    public static final String BOOKS_FILE = "books.txt";
    public static final String BORROWERS_FILE = "borrowers.txt";

    public static void addBook(Scanner scanner) {
        List<Book> books = FileHandler.readBooks(BOOKS_FILE);
        List<Borrower> borrowers = FileHandler.readBorrowers(BORROWERS_FILE);

        System.out.print("Enter Book ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Book Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Author: ");
        String author = scanner.nextLine();
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();

        // Check if a book with the same ID, title, author, or ISBN already exists
        for(Book book : books) {
            if(book.isbn.equals(isbn) || book.title.equals(title) || book.author.equals(author) || book.id == id) {
                System.out.println("Book already exists");
                return;

            }
        }

        Book book = new Book(id, title, author, isbn, "Available");
        books.add(book);

        // Save the updated book and borrower lists back to the files
        FileHandler.saveBooks(BOOKS_FILE, books);
        FileHandler.saveBorrowers(BORROWERS_FILE, borrowers);
        System.out.println("Book added successfully.");

    }


    
    public static void issueBook(Scanner scanner) {

        // Load books and borrowers from files
        List<Book> books = FileHandler.readBooks(BOOKS_FILE);
        List<Borrower> borrowers = FileHandler.readBorrowers(BORROWERS_FILE);

        System.out.print("Enter Borrower ID: ");
        int borrowerId = Integer.parseInt(scanner.nextLine());

        //Search for borrower in the existing list
        Borrower foundBorrower = null;
        for (Borrower br : borrowers) {
            if (br.id == borrowerId) {
                foundBorrower = br;
                break;
            }
        }

        // If borrower is not found, prompt to add new borrower
        if (foundBorrower == null) {

            System.out.print("Borrower not found. Enter Borrower Name to add new: ");
            String name = scanner.nextLine();

            foundBorrower = new Borrower(borrowerId, name, "None");
            borrowers.add(foundBorrower);
            System.out.println("New borrower added.");
        }

        else if (!foundBorrower.borrowedBookId.equalsIgnoreCase("None")) {
            System.out.println("This borrower already has a borrowed book.");
            return;
        }

        System.out.print("Enter Book ID to issue: ");
        int bookId = Integer.parseInt(scanner.nextLine());

        Book foundBook = null;
        for (Book b : books) {
            if (b.id == bookId && b.status.equalsIgnoreCase("Available")) {
                foundBook = b;
                break;
            }
        }

        if (foundBook != null) {
            foundBook.status = "Borrowed";
            foundBorrower.borrowedBookId = String.valueOf(foundBook.id);
            FileHandler.saveBooks(BOOKS_FILE, books);
            FileHandler.saveBorrowers(BORROWERS_FILE, borrowers);
            System.out.println("Book issued successfully.");
        } else {
            System.out.println("Book not available or does not exist.");
        }
    }
    
  
    
    

     public static void returnBook(Scanner scanner) {
        List<Book> books = FileHandler.readBooks(BOOKS_FILE);
        List<Borrower> borrowers = FileHandler.readBorrowers(BORROWERS_FILE);

        System.out.print("Enter Borrower ID: ");
        int borrowerId = Integer.parseInt(scanner.nextLine());

        Borrower foundBorrower = null;
        for (Borrower borrower : borrowers) {
            if (borrower.id == borrowerId) {
                foundBorrower = borrower;
                break;
            }
        }

        if (foundBorrower == null) {
            System.out.println("Borrower not found.");
            return;
        }

        // If the borrower hasn't borrowed any book
        if (foundBorrower.borrowedBookId.equalsIgnoreCase("None")) {
            System.out.println("This borrower hasn't borrowed any book.");
            return;
        }

         // Retrieve the ID of the book being returned
        int returnedBookId = Integer.parseInt(foundBorrower.borrowedBookId);
        Book returnedBook = null;
        for (Book book : books) {
            if (book.id == returnedBookId) {
                returnedBook = book;
                break;
            }
        }

        if (returnedBook != null) {
            returnedBook.status = "Available";
            foundBorrower.borrowedBookId = "None";
            System.out.println("Book returned successfully.");

            // Ask if the book was overdue
            System.out.print("Was the book returned late? (yes/no): ");
            String lateResponse = scanner.nextLine().trim().toLowerCase();

            if (lateResponse.equalsIgnoreCase("yes")) {
                System.out.print("Enter number of days late: ");
                int daysLate = Integer.parseInt(scanner.nextLine());
                calculateFine(daysLate); // Calculate fine based on delay
            }

            FileHandler.saveBooks(BOOKS_FILE, books);
            FileHandler.saveBorrowers(BORROWERS_FILE, borrowers);
        }

        else {
            System.out.println("Book record not found.");
        }
    }

    // Method to calculate and display fine for late return
    public static void calculateFine(int daysLate) {
        int finePerDay = 1;
        int totalFine = daysLate * finePerDay;
        System.out.println("Total fine: $" + totalFine);
    }
    
}