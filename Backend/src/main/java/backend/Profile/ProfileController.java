package backend.Profile;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.*;

import backend.Users.*;

import java.util.List;
import java.util.Optional;

/**
 * @author Soma Germano
 * this class handles the CRUD operations of a profile object
 */
@RestController
@Tag(name = "Profile Controller", description = "//TODO Soma")
public class ProfileController {

    @Autowired
    ProfileRepository profRepo;
    @Autowired
    UserRepository userRepo;

    /**
     * Create User Profile referencing User from database
     * @param user
     * @return
     */
    @PostMapping("/profile")
    public String createUserProfile(@RequestBody User user){
        //retrieve user info from database-repo
        //check
         Optional<User> existingUser = userRepo.findById(user.getId());
        if(existingUser.isPresent()) {
            Profile newprof = new Profile(user);
            profRepo.save(newprof);
            return "saved new prof to repo";
        }else{
            return "user doesnt exist";
        }
        //use pfp filepath and save into database

        //
    }

    /**
     * Retrieves all info for the user's profile
     * @param id
     * @return
     */
    @GetMapping("/profile/{id}")
    public ResponseEntity<Profile> getUserProfile(@PathVariable int id) {
        Optional<Profile> profile = profRepo.findById(id);

        if (profile.isPresent()) {
            return ResponseEntity.ok(profile.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }



    /**
     * updates profile info  like username and email
     * @param id
     * @param updatedProfile
     * @return
     */
    @PutMapping("/profile/{id}")
    public ResponseEntity<String> updateProfile(@PathVariable int id, @RequestBody Profile updatedProfile) {
        Optional<Profile> existingProfile = profRepo.findById(id);

        if (existingProfile.isPresent()) {
            Profile profile = existingProfile.get();

            // Update fields
            profile.setUser(updatedProfile.getUser());
            profile.setEmail(updatedProfile.getUser());
            // Add more fields to update as needed

            profRepo.save(profile);
            return ResponseEntity.ok("Profile updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found.");
        }
    }


    /**
     * allows user to update profile photo by inputting a new profile photo filename
     * @param id
     * @param photoFilePath
     * @return
     */
    @PutMapping("/{id}/photo")
    public ResponseEntity<String> updateProfilePhoto(@PathVariable int id, @RequestParam String photoFilePath) {
        Optional<Profile> existingProfile = profRepo.findById(id);

        if (existingProfile.isPresent()) {
            Profile profile = existingProfile.get();
            profile.setProfileImage(photoFilePath);
            profRepo.save(profile);
            return ResponseEntity.ok("Profile photo updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found.");
        }
    }

    /**
     * deletes a profile from the database by id
     * @param id
     * @return
     */
    @DeleteMapping("/profile/{id}")
    public String deleteProfile(@PathVariable int id){
        //check if user is in databse
        Optional<Profile> existingProf = profRepo.findById(id);
        if(existingProf.isPresent()){
            profRepo.deleteById(existingProf.get().getProfileId());
            return "user profile deleted";
        }else{
            return "profile not found/dne";
        }


    }

    /**
     * lists all profiles in the database
     * @return
     */
    @GetMapping("/profile/all")
    public List<Profile> getAllProfiles(){
        return profRepo.findAll();
    }

    //helper method to store file -> moved to user class
}
