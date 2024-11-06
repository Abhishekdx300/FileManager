package AccessControl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class FileMetadata {
    private String owner;
    private String group;
    private int userPermission;
    private int groupPermission;
    private int othersPermission;
    private LocalDateTime modifiedAt;

    public FileMetadata(String owner, String group) {
        this.owner = owner;
        this.group = group;
        this.userPermission = 6;
        this.groupPermission =4;
        this.othersPermission = 4;
        this.modifiedAt = LocalDateTime.now();
    }

    @JsonCreator
    public FileMetadata(
            @JsonProperty("owner") String owner,
            @JsonProperty("group") String group,
            @JsonProperty("userPermission") int userPermission,
            @JsonProperty("groupPermission") int groupPermission,
            @JsonProperty("othersPermission") int othersPermission,
            @JsonProperty("modifiedAt") LocalDateTime modifiedAt
    ) {
        this.owner = owner;
        this.group = group;
        this.userPermission = userPermission;
        this.groupPermission = groupPermission;
        this.othersPermission = othersPermission;
        this.modifiedAt = modifiedAt;
    }

    public void setMode(int userMode, int groupMode, int othersMode) {
        this.userPermission = userMode;
        this.groupPermission = groupMode;
        this.othersPermission = othersMode;
        this.modifiedAt = LocalDateTime.now();
    }
}