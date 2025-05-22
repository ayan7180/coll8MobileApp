package backend.Chat.websocket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Integer> {

    void save(String group);

    Group save(Group group);


    List<Group> findAll();

    //FIND BY NAME FOR VERIFICATION
    Optional<Group> findOptionalByGroupName(String groupName);

   Group findByGroupName(String groupName);

    @Query("SELECT g.groupMembers FROM Group g WHERE g.groupName = :groupName")
    List<String> findGroupMembersByGroupName(@Param("groupName") String groupName);

}
