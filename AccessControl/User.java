package AccessControl;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import java.util.HashSet;
import java.util.Set;

@Getter
public class User {
    private String username;
    private String password;
    private String role;
    private Set<String> groups;
    private int fileNo;

    @JsonCreator
    public User(
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("role") String role,
            @JsonProperty("groups") Set<String>groups,
            @JsonProperty("fileNo") int fileNo
    ) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.groups = groups;
        this.fileNo=fileNo;
    }

    public User(String username,String password){
        this.username = username;
        this.password = password;
        this.role = "user";
        this.groups = new HashSet<>();
        this.fileNo=0;
    }

    public boolean isPartOf(String group){
        return  groups.contains(group);
    }

    public void addGroup(String group){groups.add(group);}
    public void removeGroup(String group){groups.remove(group);}
    public void incrementFileNo(){this.fileNo+=1;}
    public void decrementFileNo(){this.fileNo-=1;}


}
