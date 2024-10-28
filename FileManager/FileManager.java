package FileManager;

import java.io.*;
import java.util.Scanner;


public class FileManager {
    private String currentPath;
    private final String rootPath;

    // TODO: Add all necessary components here..so we can use them in the operation function.

    public FileManager() {
        String tempPath = System.getProperty("user.dir") + "/fs/root";
        this.rootPath = tempPath.replace("\\", "/");
        File rootDir = new File(rootPath);
        if (!rootDir.exists()) {
            rootDir.mkdir();
        }
        this.currentPath = rootPath;

    }

    public String readFile(String fileName) {
            String filePath = currentPath + "/" + fileName;
            if (!fileName.endsWith(".txt")) {
                return "Error: Only .txt files are supported";
            }

            File file = new File(filePath);
            if (!file.exists()) {
                return "Error: File does not exist";
            }

        try (Scanner scanner = new Scanner(new FileInputStream(file))) {
            StringBuilder content = new StringBuilder();
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append("\n");
            }
            return content.toString();
            } catch (IOException e) {
                return "Error reading file: " + e.getMessage();
            }
    }

    public String writeFile(String fileName, String content) {
            String filePath = currentPath + "/" + fileName;
            if (!fileName.endsWith(".txt")) {
                return "Error: Only .txt files are supported";
            }

            File file = new File(filePath);
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                 Writer writer = new FileWriter(file)) {

                randomAccessFile.setLength(0);
                writer.write(content);
                writer.flush();
                return "File written successfully";
            } catch (Exception e) {
                return "Error writing to file: " + e.getMessage();
            }
    }

    public String deleteFile(String fileName) {
            String filePath = currentPath + "/" + fileName;
            if (!fileName.endsWith(".txt")) {
                return "Error: Only .txt files are supported";
            }

            File file = new File(filePath);
            if (!file.exists()) {
                return "Error: File does not exist";
            }

            try {
                if (file.delete()) {
                    return "File deleted successfully";
                } else {
                    return "Error: Unable to delete file";
                }
            } catch (Exception e) {
                return "Error deleting file: " + e.getMessage();
            }
    }

    public void createFile(String fileName) {
        try {
            File file = new File(currentPath + "/" + fileName);
            if (file.createNewFile()) {
                System.out.println("File created successfully: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file.");
            e.printStackTrace();
        }
    }

    public void createDirectory(String dirName) {
        File directory = new File(currentPath + "/" + dirName);
        if (directory.mkdir()) {
            System.out.println("Directory created successfully: " + directory.getName());
        } else {
            System.out.println("Failed to create directory or directory already exists.");
        }
    }

    public void changeDirectory(String path) {
        String newPath;

        if (path.equals("..")) {
            // Move to parent directory, but not above root
            File currentDir = new File(currentPath);
            String parentPath = currentDir.getParent();
            if (currentDir.getPath().endsWith("root")) {
                System.out.println("Cannot go above root directory!");
                return;
            } else {
                newPath = parentPath.replace("\\", "/");
            }
        } else if (path.equals("~")) {
            // Return to root directory
            newPath = rootPath;
        } else {
            // Navigate to specified directory
            newPath = currentPath + "/" + path;
        }

        File newDir = new File(newPath);
        if (newDir.exists() && newDir.isDirectory()) {
            currentPath = newPath;
            System.out.println("Changed directory to: " + getRelativePath());
        } else {
            System.out.println("Directory does not exist!");
        }
    }

    public void listFiles() {
        File directory = new File(currentPath);
        System.out.println("\nCurrent Directory: " + getRelativePath());
        System.out.println("\nFiles and Directories:\n");

        File[] filesList = directory.listFiles();
        if (filesList != null) {
            // Add parent directory option if not in root
            if (!currentPath.equals(rootPath)) {
                System.out.printf("%-20s %-10s\n", "..", "<DIR>");
            }

            for (File file : filesList) {
                System.out.printf("%-20s %-10s %-10d bytes\n",
                        file.getName(),
                        file.isDirectory() ? "<DIR>" : "<FILE>",
                        file.length());
            }
        }
    }

    public void printWorkingDirectory() {
        System.out.println(getRelativePath());
    }

    public String getRelativePath() {
        if (currentPath.equals(rootPath)) {
            return "/";
        }
        return "/" + currentPath.substring(rootPath.length() + 1).replace("\\", "/");
    }

}
