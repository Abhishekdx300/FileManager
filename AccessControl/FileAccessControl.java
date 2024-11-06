package AccessControl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class FileAccessControl {
    private final Map<String, User> users;
    private final Map<String, Group> groups;
    private final Map<String, FileMetadata> fileMetadataMap;
    private User currentUser;

    public FileAccessControl(
            Map<String, User> users,
            Map<String, Group> groups,
            Map<String, FileMetadata> fileMetadata
    ) {
        Map<String, User> filteredUsers = users.entrySet()
                .stream()
                .filter(entry -> (entry.getValue().getFileNo() != 0 || entry.getValue().getRole().equals("admin")))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        Map<String, Group> filteredGroups = groups.entrySet()
                .stream()
                .filter(entry -> (entry.getValue().getFileNo() != 0 || !entry.getValue().getMembers().isEmpty()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        this.users = new ConcurrentHashMap<>(filteredUsers);
        this.groups = new ConcurrentHashMap<>(filteredGroups);
        this.fileMetadataMap = new ConcurrentHashMap<>(fileMetadata);
    }


    public boolean authenticate(String username, String password) {
        if(username.isEmpty() || password.isEmpty()) return false;
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            this.currentUser = user;
            return true;
        }
        return false;
    }


    public void logout() {
        this.currentUser = null;
    }


    public boolean isAuthenticated() {
        return currentUser != null;
    }


    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }


    public FileMetadata createFileMetadata(String filename) {
        if (!isAuthenticated()) {
            throw new SecurityException("Not authenticated");
        }

        FileMetadata metadata = new FileMetadata(
                currentUser.getUsername(),
                ""
        );
        fileMetadataMap.put(filename, metadata);
        return metadata;
    }


    public boolean checkPermission(String filename, FileOperation operation) {
        if (!isAuthenticated()) {
            return false;
        }


        FileMetadata metadata = fileMetadataMap.get(filename);
        if (metadata == null) {
            return false;
        }

        // Admin users have full access
        if (currentUser.getRole().equals("admin")) {
            return true;
        }
        // Check owner permissions
        if (metadata.getOwner().equals(currentUser.getUsername())) {
            return checkPermissionForOperation(metadata.getUserPermission(), operation);
        }
        String group = metadata.getGroup(); // can be empty
        // Check group permissions
        if (!group.isEmpty() && groups.get(group).hasMember(currentUser.getUsername())) {
            return checkPermissionForOperation(metadata.getGroupPermission(), operation);
        }

        // Check others permissions
        return checkPermissionForOperation(metadata.getOthersPermission(), operation);
    }


    public boolean chmod(String filename, int userMode, int groupMode, int othersMode) {
        // Only owner or admin can change permissions
        FileMetadata metadata = fileMetadataMap.get(filename);
        if (!isAuthenticated() || metadata == null || !isOwnerOrAdmin(metadata)) {
            return false;
        }
        metadata.setMode(userMode, groupMode, othersMode);
        return true;
    }


    public boolean changeOwner(String filename, String newOwner) {
        if (!isAuthenticated() || !users.containsKey(newOwner) ) {
            return false;
        }

        FileMetadata metadata = fileMetadataMap.get(filename);
        if (metadata == null) {
            return false;
        }
        // Only admin/owner can change ownership
        if (!currentUser.getRole().equals("admin") && !currentUser.getUsername().equals(metadata.getOwner())) {
            return false;
        }
        users.get(metadata.getOwner()).decrementFileNo();
        users.get(newOwner).incrementFileNo();
        metadata.setOwner(newOwner);
        return true;
    }
    public boolean changeGroup(String filename, String newGroup) {
        if (!isAuthenticated() || !groups.containsKey(newGroup) ) {
            return false;
        }

        FileMetadata metadata = fileMetadataMap.get(filename);
        if (metadata == null) {
            return false;
        }
        // Only admin/owner can change ownership
        if (!currentUser.getRole().equals("admin") && !currentUser.getUsername().equals(metadata.getOwner())) {
            return false;
        }
        groups.get(metadata.getGroup()).decrementFileNo();
        groups.get(newGroup).incrementFileNo();
        metadata.setGroup(newGroup);
        return true;
    }

    // only admin can create user???
    public boolean addUser(String username, String password) {
        if (!isAuthenticated() || !currentUser.getRole().equals("admin")) {
            return false;
        }
        User alreadyPresent = users.get(username);
        if(alreadyPresent!=null) return false;
        users.put(username, new User(username,password));
        return true;
    }

    public boolean addGroup(String groupName) {
        if (!isAuthenticated() || !currentUser.getRole().equals("admin")) {
            return false;
        }
        Group alreadyPresent = groups.get(groupName);
        if(alreadyPresent!=null) return false;
        groups.put(groupName, new Group(groupName));
        return true;
    }

    public boolean addUserToGroup(String username, String groupName) {
        if (!isAuthenticated() || !currentUser.getRole().equals("admin")) {
            return false;
        }
        User user = users.get(username);
        Group group = groups.get(groupName);
        if (user == null || group == null) {
            return false;
        }
        group.addMember(username);
        user.addGroup(groupName);
        return true;
    }

    public boolean removeUserFromGroup(String username, String groupName) {
        if (!isAuthenticated() || !currentUser.getRole().equals("admin")) {
            return false;
        }
        User user = users.get(username);
        Group group = groups.get(groupName);
        if (user == null || group == null) {
            return false;
        }
        group.removeMember(username);
        user.removeGroup(groupName);
        return true;
    }

    public Optional<FileMetadata> getFileMetadata(String filename) {
        return Optional.ofNullable(fileMetadataMap.get(filename));
    }

    public void removeFileMetadata(String filename){
        this.fileMetadataMap.remove(filename);
    }

    public void updateUserGroupFileNo(FileMetadata fileMetadata, int val){
        this.changeUserFileNo(fileMetadata.getOwner(),val);
        this.changeGroupFileNo(fileMetadata.getGroup(),val);
    }

    // val = 1 or -1
    private void changeUserFileNo(String usr, int val){
        if(usr.isEmpty()) return;
        User user = this.users.getOrDefault(usr,null);
        if(user==null) return;
        if(val==1) user.incrementFileNo();
        else user.decrementFileNo();
    }
    private void changeGroupFileNo(String grp, int val){
        if(grp.isEmpty()) return;
        Group group = this.groups.getOrDefault(grp,null);
        if(group==null) return;
        if(val==1) group.incrementFileNo();
        else group.decrementFileNo();
    }

    private boolean isOwnerOrAdmin(FileMetadata metadata) {
        return currentUser.getRole().equals("admin") ||
                metadata.getOwner().equals(currentUser.getUsername());
    }

    private boolean checkPermissionForOperation(int permission, FileOperation operation) {
        return switch (operation) {
            case READ -> (permission & 4) != 0;
            case WRITE -> (permission & 2) != 0;
            case EXECUTE -> (permission & 1) !=0;
        };
    }
}