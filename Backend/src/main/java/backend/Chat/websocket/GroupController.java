package backend.Chat.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class GroupController {
    private final Map<String, List<String>> groupMap;

    @Autowired
    GroupRepository groupRepository;

    public GroupController(Map<String, List<String>> groupMap, GroupRepository groupRepository) {
        this.groupMap = groupMap;
        this.groupRepository = groupRepository;
    }

//    public GroupController(){
//        return ChatSocket.getGroups();
//    }


    /**
     * this method returns a list of groups created
     * @return
     */
    @GetMapping(path="/groups")
    public List<Group> getAllGroups(){
        //return new ArrayList<>(groupMap.keySet());
        //return ChatSocket.getGroups();
        return groupRepository.findAll();
    }

    @GetMapping("/groups/{groupName}")
    public ResponseEntity<List<String>> getMembers(@PathVariable String groupName) {
        // validate the groupName
        if (groupName == null || groupName.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(List.of("Group name cannot be null or empty."));
        }

        Optional<Group> groupOptional = groupRepository.findOptionalByGroupName(groupName);
        if (groupOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(List.of("Group not found."));
        }


        List<String> groupMembers = groupRepository.findGroupMembersByGroupName(groupName);
        if(groupMembers.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(groupMembers);
    }

   //TODO: REMOVE UNUSED
//    @GetMapping("/groups/{groupName}/members")
//    public ResponseEntity<List<String>> getGroupMembers(@PathVariable String groupName) {
//        if (!ChatSocket.getGroupMap().containsKey(groupName)) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(null); // Group not found
//        }
//
//        List<String> members = ChatSocket.getGroupMembers(groupName);
//        return ResponseEntity.ok(members);
//    }

//    @GetMapping("/groups/{groupName}/members")
//    public ResponseEntity<List<String>> getGroupMembers(@PathVariable String groupName) {
//        List<String> members = ChatSocket.getGroupMembers(groupName);
//        if (members == null || members.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//        return ResponseEntity.ok(members);
//    }

     @PostMapping("/groups/{groupName}/{username}")
     public String joinGroup(@PathVariable String groupName, @PathVariable String username){
        //check if group exists
         if(groupName==null || groupName.trim().isEmpty()){
             return "GroupName is null or empty, FIX";
         }
         //TODO: Validate if GC exists or not
         Optional<Group> existingGroup =groupRepository.findOptionalByGroupName(groupName);
         if(existingGroup.isPresent()){
            // return "Group "+ groupName +" already exists.";

             //join group - add to list<stirng> of members and increment membercount
             Group gp1 = groupRepository.findByGroupName(groupName);
             gp1.addGroupMembers(username);
             groupRepository.save(gp1);
             return "Joined Group:" + groupName;
         }


         return "";
     }


    /**
     * creates groups
     * @param groupName
     * @return
     * example request:
     */
    //TODO: take parameter of String username
    @PostMapping(path="/groups/{groupName}")
    public ResponseEntity<String> createGroup(@PathVariable String groupName){

        if(groupName==null || groupName.trim().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("GroupName is null or empty, FIX");
        }

        //TODO: Validate if GC exists or not
        Optional<Group> existingGroup =groupRepository.findOptionalByGroupName(groupName);
        if(existingGroup.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Group "+ groupName +" already exists.");
        }

        Group group1 = new Group();
        group1.setGroupName(groupName);
        //ChatSocket.createGroup(groupName);
        groupRepository.save(group1);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Group "+groupName+" created successfully");
    }


}
