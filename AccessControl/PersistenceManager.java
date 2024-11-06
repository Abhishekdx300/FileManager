package AccessControl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class PersistenceManager {
    private final Path configDirectory;
    private final ObjectMapper objectMapper;
    private final Path usersFile;
    private final Path groupsFile;
    private final Path metadataFile;

    public PersistenceManager() {
        String tempPath = System.getProperty("user.dir") + "/fs/config";
        this.configDirectory = Paths.get(tempPath);

        this.usersFile = configDirectory.resolve("users.json");
        this.groupsFile = configDirectory.resolve("groups.json");
        this.metadataFile = configDirectory.resolve("metadata.json");

        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        createDirectoriesIfNeeded();
    }

    private void createDirectoriesIfNeeded() {
        try {
            Files.createDirectories(configDirectory);
            createFileIfNotExists(usersFile);
            createFileIfNotExists(groupsFile);
            createFileIfNotExists(metadataFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize storage", e);
        }
    }
    private void createFileIfNotExists(Path file) throws IOException {
        if (!Files.exists(file)) {
            Files.write(file, "{}".getBytes());
        }
    }


    // Load

    public Map<String, User> loadUsers() {
        return load("users.json", new TypeReference<Map<String, User>>() {});
    }

    public Map<String, Group> loadGroups() {
        return load("groups.json", new TypeReference<Map<String, Group>>() {});
    }

    public Map<String, FileMetadata> loadFileMetadata() {
        return load("metadata.json", new TypeReference<Map<String, FileMetadata>>() {});
    }

    private <T> T load(String filename, TypeReference<T> typeRef) {
        Path filePath = configDirectory.resolve(filename);
        if (!Files.exists(filePath)) {
            return (T) new HashMap<String, Object>();
        }

        try {
            return objectMapper.readValue(filePath.toFile(), typeRef);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load data: " + filename, e);
        }
    }


    // Save

    public void saveUsers(Map<String, User> users) {
        save(users, "users.json");
    }

    public void saveGroups(Map<String, Group> users) {
        save(users, "groups.json");
    }

    public void saveFileMetadata(Map<String, FileMetadata> users) {
        save(users, "metadata.json");
    }

    private <T> void save(T data, String filename) {
        try {
            Path filePath = configDirectory.resolve(filename);
            objectMapper.writeValue(filePath.toFile(), data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save data: " + filename, e);
        }
    }

    public void saveAll(Map<String, User> users, Map<String, Group> groups, Map<String, FileMetadata> fileMetadata) {
        saveUsers(users);
        saveGroups(groups);
        saveFileMetadata(fileMetadata);
    }



}