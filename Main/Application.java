package Main;

import java.util.Scanner;
import FileManager.FileManager;

public class Application {

    public static void Init(Scanner scanner, FileManager fm){
        while (true) {
            System.out.println("\n=== File Manager ===");
            System.out.println("Current location: " + fm.getRelativePath());
            System.out.println("1. Create File");
            System.out.println("2. Create Directory");
            System.out.println("3. List Files");
            System.out.println("4. Change Directory");
            System.out.println("5. Print Working Directory (PWD)");
            System.out.println("6. Read Text File");
            System.out.println("7. Write to Text File");
            System.out.println("8. Delete Text File");
            System.out.println("9. Exit");
            System.out.print("Choose an option: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        System.out.print("Enter file name: ");
                        String fileName = scanner.nextLine();
                        fm.createFile(fileName);
                        break;

                    case 2:
                        System.out.print("Enter directory name: ");
                        String dirName = scanner.nextLine();
                        fm.createDirectory(dirName);
                        break;

                    case 3:
                        fm.listFiles();
                        break;

                    case 4:
                        System.out.println("Enter directory name (.. for parent, ~ for root): ");
                        String newDir = scanner.nextLine();
                        fm.changeDirectory(newDir);
                        break;

                    case 5:
                        fm.printWorkingDirectory();
                        break;
                    case 6: // Read File
                        System.out.print("Enter file name to read (.txt): ");
                        String readFileName = scanner.nextLine();
                        try {
                            String readResult = fm.readFile(readFileName);
                            System.out.println("Reading file...");
                            System.out.println("Content:\n" + readResult);
                        } catch (Exception e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;

                    case 7: // Write File
                        System.out.print("Enter file name to write (.txt): ");
                        String writeFileName = scanner.nextLine();
                        System.out.println("Enter content (type 'END' on a new line to finish):");
                        StringBuilder content = new StringBuilder();
                        String line;
                        while (!(line = scanner.nextLine()).equals("END")) {
                            content.append(line).append("\n");
                        }
                        try {
                            String writeResult = fm.writeFile(writeFileName, content.toString());
                            System.out.println(writeResult);
                        } catch (Exception e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;

                    case 8: // Delete File
                        System.out.print("Enter file name to delete (.txt): ");
                        String deleteFileName = scanner.nextLine();
                        try {
                            String deleteResult = fm.deleteFile(deleteFileName);
                            System.out.println(deleteResult);
                        } catch (Exception e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;

                    case 9: // Exit
                        System.out.println("Exiting...");
                        scanner.close();
                        System.exit(0);

                    default:
                        System.out.println("Invalid option!");
                }
            } catch (Exception e) {
                System.out.println("Error: Invalid input");
                scanner.nextLine(); // Clear the invalid input
            }
        }
    }

    public static void main(String[] args) {
        FileManager fm = new FileManager();
        Scanner scanner = new Scanner(System.in);

        Init(scanner,fm);

    }
}
