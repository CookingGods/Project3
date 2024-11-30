import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Create an instance of BTreeIndex to manage B-tree operations
        BTreeIndex btree = new BTreeIndex();
        Scanner scanner = new Scanner(System.in);

        // Main loop for user interaction
        while (true) {
            // Display available commands to the user
            System.out.println("\nAvailable commands:");
            System.out.println("create  - Create a new index file");
            System.out.println("open    - Open an existing index file");
            System.out.println("insert  - Insert a key-value pair");
            System.out.println("search  - Search for a key");
            System.out.println("load    - Load key-value pairs from file");
            System.out.println("print   - Print all key-value pairs");
            System.out.println("extract - Save all key-value pairs to file");
            System.out.println("quit    - Exit program");

            // Prompt user for input
            System.out.print("\nEnter command: ");
            String command = scanner.nextLine().toLowerCase();

            try {
                switch (command) {
                    case "create":
                        // Create a new index file
                        System.out.print("Enter filename: ");
                        String createFile = scanner.nextLine();
                        if (btree.createFile(createFile)) {
                            System.out.println("File created successfully");
                        }
                        break;

                    case "open":
                        // Open an existing index file
                        System.out.print("Enter filename: ");
                        String openFile = scanner.nextLine();
                        if (btree.openFile(openFile)) {
                            System.out.println("File opened successfully");
                        }
                        break;

                    case "insert":
                        // Insert a new key-value pair into the B-tree
                        System.out.print("Enter key (unsigned integer): ");
                        long key = Long.parseLong(scanner.nextLine());
                        System.out.print("Enter value (unsigned integer): ");
                        long value = Long.parseLong(scanner.nextLine());

                        // Ensure non-negative inputs
                        if (key < 0 || value < 0) {
                            System.out.println("Error: Values must be non-negative");
                            break;
                        }

                        // Insert the key-value pair
                        btree.insert(key, value);
                        break;

                    case "search":
                        // Search for a specific key in the B-tree
                        System.out.print("Enter key to search for: ");
                        long searchKey = Long.parseLong(scanner.nextLine());

                        // Ensure non-negative key
                        if (searchKey < 0) {
                            System.out.println("Error: Key must be non-negative");
                            break;
                        }

                        // Retrieve and display the search result
                        Long[] result = btree.search(searchKey);
                        if (result != null) {
                            System.out.println("Found: key=" + result[0] + ", value=" + result[1]);
                        } else {
                            System.out.println("Key not found");
                        }
                        break;

                    case "load":
                        // Load key-value pairs from a file into the B-tree
                        System.out.print("Enter input filename: ");
                        String loadFile = scanner.nextLine();
                        btree.load(loadFile);
                        break;

                    case "print":
                        // Print all key-value pairs in the B-tree
                        btree.print();
                        break;

                    case "extract":
                        // Save all key-value pairs from the B-tree to a file
                        System.out.print("Enter output filename: ");
                        String extractFile = scanner.nextLine();
                        btree.extract(extractFile);
                        break;

                    case "quit":
                        // Close resources and exit the program
                        btree.close();
                        scanner.close();
                        System.out.println("Goodbye!");
                        return;

                    default:
                        // Handle unknown commands
                        System.out.println("Unknown command. Please try again.");
                        break;
                }
            } catch (IOException e) {
                // Handle I/O errors
                System.out.println("Error: I/O error occurred - " + e.getMessage());
            } catch (NumberFormatException e) {
                // Handle invalid numeric input
                System.out.println("Error: Invalid number format");
            } catch (Exception e) {
                // Catch-all for other exceptions
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
