package backend.Chat.websocket;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "groups")
@Data //bundles up annotations: Getter, Setter, ToString, EqualsAndHashCode, and RequiredArgsConstructor together
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String groupName;

    @Column
    private int memberCount;

    @ElementCollection
    @CollectionTable(name = "group_members", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "member_name")
    private List<String> groupMembers;

    //default
    public Group(){ }

    //constructor
    public Group(String groupName){
        this.groupName = groupName;
        this.memberCount = 0;
        this.groupMembers = new ArrayList<>();
    }

    //getters and setters
    public int getId() { return id; }
    public void setId(int id) {  this.id = id;  }

    public List<String> getGroupMembers() {
        return groupMembers;
    }

    public void addGroupMembers(String memberName){
        this.groupMembers.add(memberName);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }
    public void incrementMemberCount(){
        int memberCount1 = this.memberCount;
        memberCount1++;
    }
    public int getMemberCount() {
        return memberCount;
    }
}
