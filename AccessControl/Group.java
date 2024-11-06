package AccessControl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Group {
    private String name;
    private Set<String> members;
    private int fileNo;

    public Group(String name) {
        this.name = name;
        this.members = new HashSet<>();
        this.fileNo=0;
    }

    @JsonCreator
    public Group(
            @JsonProperty("name") String name,
            @JsonProperty("members") Set<String> members,
            @JsonProperty("fileNo") int fileNo
    ) {
        this.name = name;
        this.members = members;
        this.fileNo = fileNo;
    }


    public void addMember(String username) {
        members.add(username);
    }

    public boolean hasMember(String username) {
        return members.contains(username);
    }
    public void removeMember(String username){
            members.remove(username);
    }
    public void incrementFileNo(){this.fileNo+=1;}
    public void decrementFileNo(){this.fileNo-=1;}
}