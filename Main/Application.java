package Main;

import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;
import FileManager.FileManager;

public class Application {
    // delete user & group -- need to store reference to file to delete files or remove groupinfo from metadata and user
    public static void Init(Scanner scanner, FileManager fm){
        while (true) {
            if(fm.isAuthenticated()){
                System.out.println("\n=== File Manager ===");
                System.out.println("Current location: " + fm.getRelativePath());
                System.out.println("1. Create File"); // -
                System.out.println("2. Create Directory");
                System.out.println("3. List Files");
                System.out.println("4. Change Directory");
                System.out.println("5. Print Working Directory (PWD)");
                System.out.println("6. Read Text File"); // -
                System.out.println("7. Write to Text File");// -
                System.out.println("8. Delete Text File");// -
                System.out.println("9. Create User"); // only admin role can do it -
                System.out.println("10. Create Group"); // any user can -- (grp w. 0 user but file pointing it? -- add) -
                System.out.println("11. Add User to Group"); // only admin -
                System.out.println("12. remove User from grp"); // only admin can do it -- grp mem 0 delete grp -- make filemeta grp null -
                System.out.println("13. change file access mode"); // only admin or the owner can do it -
                System.out.println("14. change file owner"); // only admin or the owner can do it -
                System.out.println("15. change file grp"); // only admin or the owner can do it -
                System.out.println("16. logout"); // -
                System.out.println("17. Exit");
                System.out.print("Choose an option: ");
                // recheck 12


                try {
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    switch (choice) {
                        case 1:
                            System.out.print("Enter file name: ");
                            String fileName = scanner.nextLine();
                            boolean fileCreated = fm.createFile(fileName);
                            if(fileCreated) System.out.println("file created successfully.");
                            else System.out.println("failed to create file.");
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
                            if(!readFileName.endsWith(".txt")){
                                System.out.println("Invalid filetype");
                                break;
                            }
                            try {
                                Optional<String> readResult = fm.readFile(readFileName);
                                if(readResult.isEmpty()){
                                    System.out.println("Either file is empty or some error occurred.");
                                }else{
                                    System.out.println("Content:\n" + readResult.get());
                                }
                            } catch (Exception e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                            break;

                        case 7: // Write File
                            System.out.print("Enter file name to write (.txt): ");
                            String writeFileName = scanner.nextLine();
                            if(!writeFileName.endsWith(".txt")){
                                System.out.println("Invalid filetype");
                                break;
                            }
                            System.out.println("Enter content (type 'END' on a new line to finish):");
                            StringBuilder content = new StringBuilder();
                            String line;
                            while (!(line = scanner.nextLine()).equals("END")) {
                                content.append(line).append("\n");
                            }
                            try {
                                boolean written = fm.writeFile(writeFileName, content.toString());
                                if(written){
                                    System.out.println("File Write successful");
                                }else{
                                    System.out.println("File write failed");
                                }
                            } catch (Exception e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                            break;

                        case 8: // Delete File
                            System.out.print("Enter file name to delete (.txt): ");
                            String deleteFileName = scanner.nextLine();
                            if(!deleteFileName.endsWith(".txt")){
                                System.out.println("Invalid filetype");
                                break;
                            }
                            try {
                                boolean deleted = fm.deleteFile(deleteFileName);
                                if(deleted){
                                    System.out.println("successfully deleted.");
                                }else{
                                    System.out.println("could not delete the file");
                                }
                            } catch (Exception e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                            break;
                        case 9: // New User
                            System.out.println("Enter new username: ");
                            String username = scanner.nextLine();
                            System.out.println("Enter password: ");
                            String password = scanner.nextLine();

                            boolean success = fm.createUser(username,password); // only admin role can if not already present

                            if(success) System.out.println("User created successfully");
                            else System.out.println("failed to create user");
                            break;

                        case 10: // New Group
                            System.out.println("Enter new group name: ");
                            String groupName = scanner.nextLine();
                            boolean groupCreated = fm.createGroup(groupName); // only admin role can if not already present

                            if(groupCreated) System.out.println("group created successfully");
                            else System.out.println("failed to create group");
                            break;

                        case 11: // Add user to group
                            System.out.println("Enter username: ");
                            String usr = scanner.nextLine();
                            System.out.println("Enter groupname: ");
                            String grp = scanner.nextLine();
                            boolean usrAddedToGrp = fm.addUserToGroup(usr,grp); // only admin can
                            if(usrAddedToGrp) System.out.println("Successfully added user to the group.");
                            else System.out.println("Failed to add user to group.");
                            break;

                        case 12:
                            System.out.println("Enter username: ");
                            String user = scanner.nextLine();
                            System.out.println("Enter groupname: ");
                            String group = scanner.nextLine();
                            boolean usrRemovedToGrp = fm.removeUserFromGroup(user, group); // only admin can
                            if(usrRemovedToGrp) System.out.println("Successfully removed user from group.");
                            else System.out.println("Failed to remove user from group.");
                            break;

                        case 13:
                            System.out.println("enter filename: ");
                            String filename = scanner.nextLine();
                            System.out.println("Choose sum of values as digits to give access: ");
                            System.out.println("Read : 4 ,  Write : 2, Execute : 1 ");
                            System.out.println("Input 3 digit integer for each for user, group and others respectively: ");
                            try{
                                int num = scanner.nextInt();
                                boolean modifiedMode = fm.changeFileMode(filename,num);
                                if(modifiedMode) System.out.println("File Access mode updated.");
                                else System.out.println("failed to update file access mode");
                            }catch (Exception e){
                                System.out.println("Invalid input");
                            }
                            break;

                        case 14:
                            System.out.println("enter filename: ");
                            String filename1 = scanner.nextLine();
                            System.out.println("enter new owner's username: ");
                            String newOwner = scanner.nextLine();
                            boolean modifiedOwner = fm.changeFileOwner(filename1,newOwner);
                            if(modifiedOwner) System.out.println("File Owner changed successful.");
                            else System.out.println("Failed to change file owner.");
                            break;

                        case 15:
                            System.out.println("enter filename: ");
                            String filename2 = scanner.nextLine();
                            System.out.println("enter new group's name: ");
                            String newGrp = scanner.nextLine();
                            boolean modifiedGrp = fm.changeFileGroup(filename2,newGrp);
                            if(modifiedGrp) System.out.println("File Group changed successful.");
                            else System.out.println("Failed to change file group.");
                            break;

                        case 16:
                            fm.logout();
                            System.out.println("successfully logged out.");
                            break;

                        case 17: // Exit
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
            }else{
                System.out.println("\n=== Authentication ===");
                System.out.println("1. login user");
                System.out.println("2. Exit");
                System.out.print("Choose an option: ");
                try{
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    switch (choice){
                        case 1:
                            System.out.println("Enter Username: ");
                            String username = scanner.nextLine();
                            System.out.println("Enter password: ");
                            String password = scanner.nextLine();

                            boolean success = fm.authenticate(username,password);
                            if(success) System.out.println("Successfully logged In.");
                            else System.out.println("Failed to log in.");
                            break;
                        case 2:
                            System.out.println("Exiting...");
                            scanner.close();
                            System.exit(0);
                        default:
                            System.out.println("Invalid option!");
                    }
                }catch (Exception e){
                    System.out.println("Error: Invalid input");
                    scanner.nextLine(); // Clear the invalid input
                }
            }
        }
    }

    public static void main(String[] args) {
        FileManager fm = new FileManager();
        Scanner scanner = new Scanner(System.in);

        Init(scanner,fm);

    }
}
