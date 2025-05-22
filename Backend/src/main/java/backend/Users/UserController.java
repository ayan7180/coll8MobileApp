package backend.Users;

import backend.Stubs.*;
import backend.Courses.Course;
import backend.Courses.CourseRepository;
import backend.Files.SysFile;
import backend.Files.SysFileRepository;
import backend.Majors.MajorRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.models.examples.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.*;

import backend.Users.UserRepository;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Integer.parseInt;
import backend.Profile.Profile;
import backend.Profile.ProfileRepository;

@Tag(name = "User Controller", description = "Operations for managing app users.")
/**
 * @author Soma Germano
 * this class handles the CRUD operations of the User
 * Create User
 * Read/Return a User
 * Update User info
 * Delete a User
 */
@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    // TODO Replace the courseNum and abbreviation fields of Course with a CourseStub object that will act as the key/id
    @Autowired
    CourseRepository courseRepository;

    @Autowired
    SysFileRepository fileRepository;

    @Autowired
    ProfileRepository profileRepository;


    //////////////////////////////////////////////////
    // Get Mappings
    //////////////////////////////////////////////////

    @GetMapping(path = "/user/{username}/showcase/")
    public SysFile getShowcaseFile(@PathVariable String username) {
        if (userRepository.findByUsername(username).isEmpty()) {
            return null;
        }
        User user = userRepository.findByUsername(username).get();
        return user.getShowcaseFile();
    }
    /**
     * this method assigns a profile to the given user (onetoone relationship)
     *
     * @param userId
     * @param profileId
     * @return
     */

    @PutMapping("/userprofile/{userId}/{profileId}")
    String assignProfileToUser(@PathVariable int userId, @PathVariable int profileId){
        Optional<User> user = userRepository.findById(userId);
        Optional<Profile> profile = profileRepository.findById(profileId);
        if(user.isEmpty() || profile.isEmpty()){
            return "failure";
        }
        profile.get().setUser(user.orElse(null));
        //user.setProfile(profile);
        userRepository.save(user.orElse(null));
        return "success";
       // return user.getShowcaseFile();
    }

    @GetMapping(path = "/user/{id}/courses/")
    public Set<Course> getAddedCourses(@PathVariable int id){
        // User can't be found: return nothing
        if(userRepository.findById(id).isEmpty()){
            return null;
        }
        User user = userRepository.findById(id).get();
        return user.getCourses();
    }

    /**
     * this method returns/lists all users in the database
     * @return
     */
    @GetMapping(path = "/login/all")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * the method requests a String array user that contains the user's attributes
     * this method checks that the user exists in the database
     * if user exists, returns the user
     * @param user
     * @return
     */
    @GetMapping("/login")
    public User checkUser(@RequestBody String[] user) {
        //send string
        User user1 = new User(user[0], user[1], 0,  null, null);
        int num = user1.getId();
        Optional<User> existingUser = userRepository.findById(num);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }else {
            return null;
        }

    }

    /**
     * this method retrieves the image path using a user's id
     * @param userId
     * @return
     */
    @GetMapping("/{userId}/profileImage")
    public ResponseEntity<String> getProfileImage(@PathVariable int userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String imagePath = "/images/"+ user.getProfileImage();


            return ResponseEntity.ok(imagePath); // Return path as response`
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

    //////////////////////////////////////////////////
    // Post Mappings
    //////////////////////////////////////////////////



    /**
     * Adds a class to a user's list of added classes.
     */
    @PostMapping(path = "/user/addCourse")
    public String addCourseForUser(@RequestBody UserStubs.AddCourse stub) {
        Optional<User> opUser = userRepository.findById(stub.getUserID());
        Optional<Course> opCourse = courseRepository.findById(stub.getCourseID());

        if(opUser.isEmpty()){
            return "Could not find user";
        }

        if(opCourse.isEmpty()){
            return "Couse not find course";
        }

        User user = opUser.get();
        Course course = opCourse.get();

        user.addCourse(course);
        userRepository.save(user);

        return "Success!";
    }

    /**
     * this method allows user to create a new user
     * it requests a string array user that contains a user's attributes,
     * creates a new user using those attributes,
     * and saves it to the database (checking that the user doesn't already exist)
     * @param user
     * @return
     */
    @PostMapping("/signup")
    public String saveUser(@RequestBody String[] user){
        Map<String, String> response = new HashMap<>();
        User newUser = new User();

        String username = user[0];
        String password = user[1];


        newUser.setUsername(username);
        newUser.setPassword(password);

//      check if user with the same username, password, and id exists

        Optional<User> existingUser = userRepository.findByUsername(username);
        if(existingUser.isPresent()){
            return "User already exists";
            //response.put
        }else{
            userRepository.save(newUser);
            //success message
            return "new user posted";
        }
        // return response;
    }

    /**
     * this method saves the path of the profile picture in the database
     * @param id
     * @param file
     * @return
     */
    @PostMapping("/{userId}/uploadProfileImage")
    public ResponseEntity<String> uploadProfileImage(@PathVariable int id,@RequestParam("file") MultipartFile file) {

        try {
            // Set the directory to save files`
            String uploadDir = "/images/";
            String fileName = file.getOriginalFilename();
            String filePath = uploadDir + fileName;

            File destinationFile = new File(filePath);
            file.transferTo(destinationFile);

            // Retrieve user, set profile image path, and save
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setProfileImage(filePath);
                // Save path in the database`
                userRepository.save(user);
                return ResponseEntity.ok("File uploaded successfully: " + filePath);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed.");
        }
    }

    //////////////////////////////////////////////////
    // Put Mappings
    //////////////////////////////////////////////////

    /**
     * Layout:
     * {
     *     "user": { "id", int },
     *     "file": { "id", int }
     * }
     * @param json
     * @return
     */
    @PutMapping(path = "/user/showcase/")
    public String setShowcaseFile(@RequestBody JsonNode json){
        JsonNode userJson = json.get("user");
        JsonNode fileJson = json.get("file");
        int userID = userJson.get("id").asInt();
        int fileID = fileJson.get("id").asInt();

        if(userRepository.findById(userID).isEmpty()){
            return "Could not find user";
        }
        User user = userRepository.findById(userID).get();
        if(fileRepository.findById(fileID).isEmpty()){
            return "Could not find file";
        }
        SysFile file = fileRepository.findById(fileID).get();

        user.setShowcaseFile(file);
        userRepository.save(user);

        return "Success!";
    }

    /**
     * this method assigns a profile to the given user (onetoone relationship)
     *
     * @param userId
     * @param profileId
     * @return
     */


    /**
     * this method verifies a user and then updates user's username and password
     * saves the new changes to the database
     * @param id
     * @param user
     * @return
     */
    @PutMapping(path = "/login/{id}")
    public Map<String, String> updateUser(@PathVariable Integer id, @RequestBody User user) {
        Map<String, String> response = new HashMap<>();

        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User u = existingUser.get();
            //change username and password
            u.setUsername(user.getUsername());
            u.setPassword(user.getPassword());
            //save the updated user
            userRepository.save(u);
            response.put("message", "User updated");
        }else{
            response.put("message", "user not found");
        }
        return response;
    }

    //////////////////////////////////////////////////
    // Del Mappings
    //////////////////////////////////////////////////

    @DeleteMapping(path = "/user/course")
    public String deleteClassFromUser(@RequestBody JsonNode json){
        int courseID = json.get("courseID").asInt();
        int userID = json.get("userID").asInt();

        if(userRepository.findById(userID).isEmpty()){
            return "Could not find user";
        }
        User user = userRepository.findById(userID).get();
        if(courseRepository.findById(courseID).isEmpty()){
            return "Could not find course";
        }
        Course course = courseRepository.findById(courseID).get();

        user.removeCourse(course);
        userRepository.save(user);

        return "Success";
    }

    /**
     * this method verifies that user exists and then deletes user instance by the id given in the parameter
     * @param id
     * @return
     */
    @DeleteMapping("login/{id}")
    public Map<String, String> deleteUser(@PathVariable Integer id) {
        Map<String, String> response = new HashMap<>();

        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            userRepository.deleteById(id);
            response.put("message", "User deleted");
        }else{
            response.put("message", "user not found");
        }
        return response;
    }



    //////////////////////////////////////////////////
    // Other Methods
    //////////////////////////////////////////////////

    /**
     * this method logs in a user by checking that they exist in the database and returns the user
     * the method requests an object instance of the user class
     * @param user
     * @return
     */
   //read
    @RequestMapping(path = "/login")
    public User loginUser(@RequestBody User user) { //maybe change parameter to be String[] a
        Optional<User> u = userRepository.findById(user.getId());
        return u.orElse(null);
    }

}
