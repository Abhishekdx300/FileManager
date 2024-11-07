package FilesManager;

import AccessControl.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;


@Slf4j
public class FileManager {
    private String currentPath;
    private final String rootPath;
    private final PersistenceManager persistenceManager;
    private final FileAccessControl accessControl;

    // TODO: Add all necessary components here..so we can use them in the operation function.

    public FileManager() {
        String tempPath = System.getProperty("user.dir") + "/fs/root";
        this.rootPath = tempPath.replace("\\", "/");
        File rootDir = new File(rootPath);
        if (!rootDir.exists()) {
            boolean created = rootDir.mkdir();
            if(!created) throw new RuntimeException("Failed to initialize root directory");
        }
        this.currentPath = rootPath;
        this.persistenceManager = new PersistenceManager();
        this.accessControl = new FileAccessControl(
                persistenceManager.loadUsers(),
                persistenceManager.loadGroups(),
                persistenceManager.loadFileMetadata()
        );
        persistUserData();
    }


    public boolean isAuthenticated(){
        return accessControl.isAuthenticated();
    }
    public boolean authenticate(String username, String password){
        return accessControl.authenticate(username,password);
    }
    public void logout(){
        accessControl.logout();
    }

    private void persistUserData(){
        accessControl.getCurrentUser().ifPresent(_-> persistenceManager.saveAll(
                accessControl.getUsers(),
                accessControl.getGroups(),
                accessControl.getFileMetadataMap()
        ));
    }
    private boolean isValidTextFile(String filename){
        return filename.endsWith(".txt") &&
                !filename.contains("/");
    }


    public Optional<String> readFile(String fileName) {
        if(!accessControl.checkPermission(fileName,FileOperation.READ)){
            return Optional.empty();
        }
            String fp = currentPath + "/" + fileName;
        try{
            Path filePath = Paths.get(fp);
            return Optional.of(Files.readString(filePath));

        }catch (IOException e){
            return Optional.empty();
        }

    }

    public boolean writeFile(String fileName, String content) {
            String filePath = currentPath + "/" + fileName;

            if(!accessControl.checkPermission(fileName,FileOperation.WRITE)){
                return false;
            }

            File file = new File(filePath);
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                 Writer writer = new FileWriter(file)) {

                randomAccessFile.setLength(0);
                writer.write(content);
                writer.flush();
                return true;
            } catch (Exception e) {
                return false;
            }
    }

    public boolean deleteFile(String fileName) {
            String filePath = currentPath + "/" + fileName;
            if(!accessControl.checkPermission(fileName,FileOperation.WRITE)){
                return false;
            }
            File file = new File(filePath);
            if (!file.exists()) {
                return false;
            }

            try {
                if (file.delete()) {
                    Optional<FileMetadata> fileMetadata = accessControl.getFileMetadata(fileName);
                    fileMetadata.ifPresent(metadata -> accessControl.updateUserGroupFileNo(metadata, -1));

                    accessControl.removeFileMetadata(fileName);
                    persistUserData();
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
    }

    public boolean createFile(String fileName) {
        if(!isValidTextFile(fileName)) return false;
        try {
            File file = new File(currentPath + "/" + fileName);
            if (file.createNewFile()) {
                FileMetadata fileMetadata = accessControl.createFileMetadata(fileName);
                accessControl.updateUserGroupFileNo(fileMetadata, 1);
                persistUserData();
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            return false;
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

    public boolean createUser(String username, String password){
        if(username.isEmpty() || password.isEmpty()) return false;
        return accessControl.addUser(username,password);
    }

    public boolean createGroup(String groupname){
        if(groupname.isEmpty()) return false;
        return accessControl.addGroup(groupname);
    }
    public boolean addUserToGroup(String username, String groupname){
        if(username.isEmpty() || groupname.isEmpty()) return false;
        return accessControl.addUserToGroup(username,groupname);
    }
    public boolean removeUserFromGroup(String username, String groupname){
        if(username.isEmpty() || groupname.isEmpty()) return false;
        return accessControl.removeUserFromGroup(username,groupname);
    }
    public boolean changeFileMode(String filename,int num){

        int usrMode = num/100;
        int grpMode = (num/10)%10;
        int othMode = num%10;
        if(filename.isEmpty() || InvalidMode(usrMode) || InvalidMode(grpMode) || InvalidMode(othMode)) return false;

        boolean result = accessControl.chmod(filename,usrMode,grpMode,othMode);
        persistUserData();
        return result;
    }
    public boolean changeFileOwner(String filename, String newOwner){
        if(filename.isEmpty() || newOwner.isEmpty()) return false;
        boolean result =  accessControl.changeOwner(filename,newOwner);
        persistUserData();
        return result;
    }
    public boolean changeFileGroup(String filename, String newGroup){
        if(filename.isEmpty() || newGroup.isEmpty()) return false;
        boolean result =  accessControl.changeGroup(filename,newGroup);
        persistUserData();
        return result;
    }

    private boolean InvalidMode(int num){
        return num < 1 || num > 7;
    }

}
