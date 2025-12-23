import java.io.*;
import java.util.*;

public class FileHandler {
    public static List<Book> readBooks(String filePath) {

        //We created an array list (resizeable array) because we do not have a definite size for txt file.
        List<Book> books = new ArrayList<>();

        // We used buffered reader instead of file reader because
        // it adds buffering to improve performance, especially useful for reading large files or lines of text.
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            // Loop through each line of the file until the end is reached.
            while ((line = reader.readLine()) != null) {

                // Skip lines that are empty or contain only whitespace
                if (!line.trim().isEmpty()) {

                    // Split the line by commas into an array of string
                    // This assumes that the file is comma-separated
                    String[] parts = line.split(",");

                    //Check if the line has at least five fields:id, title, author, isbn and status
                    if (parts.length >= 5) {

                        int id = Integer.parseInt(parts[0].trim());
                        String title = parts[1].trim();
                        String author = parts[2].trim();
                        String isbn = parts[3].trim();
                        String status = parts[4].trim();

                        // Create a new book object and add it to the list
                        books.add(new Book(id, title, author, isbn, status));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading books: " + e.getMessage());
        }
        return books;
    }

    //Method to read borrower information from a file and return a list of borrower objects
    public static List<Borrower> readBorrowers(String filePath) {
        List<Borrower> borrowers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        int id = Integer.parseInt(parts[0].trim());
                        String name = parts[1].trim();
                        String bookId = parts[2].trim();

                        borrowers.add(new Borrower(id, name, bookId));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading borrowers: " + e.getMessage());
        }
        return borrowers;
    }

    // Method to write the list of books back to a file (overwrites the file content)
   public static void saveBooks(String filePath, List<Book> books) {
   try(PrintWriter writer = new PrintWriter(new FileWriter(filePath))){

       // loop through each book in the list
    for ( Book book : books) {
        //Write the book data as a formatted string using its toFileString method
        writer.println(book.toFileString());
    }  
   } catch (IOException e) {
    System.out.println("Error saving books: " + e.getMessage());
   }
   }

   public static void saveBorrowers(String filePath, List<Borrower> borrowers) {
    try(PrintWriter writer = new PrintWriter(new FileWriter(filePath))){
        // Write each borrower as a string to the file
     for ( Borrower b : borrowers) {
         // Convert borrower to file compatible form
         writer.println(b.toFileString());
     }  
    } catch (IOException e) {
     System.out.println("Error saving borrowers: " + e.getMessage());
    }
    }
}