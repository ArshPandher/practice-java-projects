import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n===== Library Management System =====");
            System.out.println("1. Add Book");
            System.out.println("2. Issue Book");
            System.out.println("3. Return Book");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    LibraryManager.addBook(scanner);
                    break;
                case "2":
                    LibraryManager.issueBook(scanner);
                    break;
                case "3":
                    LibraryManager.returnBook(scanner);
                    break;

                case "4":
                    System.out.println("Exiting program.");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
}