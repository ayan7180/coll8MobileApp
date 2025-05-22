package backend.Users;

import backend.Stubs.*;
import backend.Courses.Course;
import backend.Files.SysFile;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;
import java.util.Set;

public class UserUtils {

    public static boolean verifyUserByStub(UserStubs.Credentials cred, UserRepository userRepo)
    throws Exception
    {
        Optional<User> opUser = userRepo.findByUsername(cred.getUsername());
        if(opUser.isEmpty()) { throw new Exception("Could not find user"); }
        User user = opUser.get();
        if(user.getPassword().equals(cred.getPassword())){
            return true;
        }
        else{
            return false;
        }
    }

    // Method that takes user credentials and a file and checks to see if that user is an owner or superuser
    public static boolean verifyFileOwner(UserStubs.Credentials cred, SysFile sysFile, UserRepository userRepository) throws Exception {
        // Verify the user has the right user/pass combo
        if(!UserUtils.verifyUserByStub(cred, userRepository)){
            throw new Exception("Incorrect user credentials");
        }

        Optional<User> opUser = userRepository.findByUsername(cred.getUsername());
        if(opUser.isEmpty()){
            throw new Exception("Couldn't find user to verify");
        }
        User user = opUser.get();

        // Verify file belongs to this user, or if they're a superuser
        User owner = sysFile.getOwner();
        if( owner.getUsername().equals(user.getUsername())){
            return true;
        }
        else if( user.isSuperuser() ){
            return true;
        }
        else{
            throw new Exception("User does not owner this file, or is not a superuser");
        }

    }

    public static boolean verifyCourseOwner(UserStubs.Credentials cred, Course course, UserRepository userRepository) throws Exception {
        // Verify the user has the right user/pass combo
        if(!UserUtils.verifyUserByStub(cred, userRepository)){
            throw new Exception("Incorrect user credentials");
        }

        // Grab user
        Optional<User> opUser = userRepository.findByUsername(cred.getUsername());
        if(opUser.isEmpty()){
            throw new Exception("User couldn't be found");
        }
        User user = opUser.get();

        // Verify course belongs to this user, or if they're a superuser
        Set<User> owners = course.getOwners();
        if( owners.contains(user) || user.isSuperuser() ){
            return true;
        }
        else{
            throw new Exception("User does not own this file, or is not a superuser");
        }

        // If no checks fail, return true.
    }

    public static boolean verifySuperuserStatus(UserStubs.Credentials cred, UserRepository userRepository) throws Exception{

        Optional<User> opUser = userRepository.findByUsername(cred.getUsername());
        if(opUser.isEmpty()) {
            throw new Exception("That username doesn't exist");
        }
        User user = opUser.get();


        // Verify the user has the right user/pass combo
        if(!UserUtils.verifyUserByStub(cred, userRepository)){
            throw new Exception("Incorrect user credentials");
        }

        else if( !user.isSuperuser() ){
            throw new Exception("User is not a superuser");
        }

        // If no checks fail, return true.
        return true;
    }

}
