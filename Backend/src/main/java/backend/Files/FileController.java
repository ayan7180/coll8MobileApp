package backend.Files;

import backend.Courses.*;
import backend.Stubs.*;
import backend.Users.UserUtils;
import backend.Users.*;
import backend.Majors.MajorRepository;
import backend.Users.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Optional;

// https://www.geeksforgeeks.org/spring-boot-file-handling/

@RestController
@Tag(name = "File Controller", description = "Handles files and SysFiles for users and courses.")
public class FileController {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    MajorRepository majorRepository;

    @Autowired
    SysFileRepository sysFileRepository;

    @Autowired
    UserRepository userRepository;

    public final static String rootPath = "/tmp/coll8/Files";
    private final String divider = "/";

    private final String fileSearch_Input = "";
    private final String fileUpload_Input = "";
    private final String fileUpdate_Input = "{ \"filename\": String }";

    private final String fileSearch_Output = "";
    private final String fileUpload_Output = "";

    //////////////////////////////////////////////////
    // Get Mappings
    //////////////////////////////////////////////////

    // Unprivileged operation
    @Operation(summary = "Retrieve a file by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The file that has been found",
                    content = {
                            @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "Resource Object built from File"))
                    }
            )
    })
    @GetMapping(path = "/file/{FileId}")
    public Resource getFileByID(@PathVariable int FileID){

        // File couldn't be found; return nothing.
        if(sysFileRepository.findById(FileID).isEmpty()){
            return null;
        }
        SysFile sysFile = sysFileRepository.findById(FileID).get();

        try {
            String filePath = sysFile.getFilePath();
            File file = new File(filePath);
            FileInputStream fileReader = new FileInputStream(file);

            return new ByteArrayResource(fileReader.readAllBytes());
        } catch(Exception e){
            return null;
        }

    }

//    // Test
//    @GetMapping(path = "/filetest")
//    public FileStubs.Search testMethod(){
//        FileStubs.Search stub = new FileStubs.Search();
//        return stub;
//    }

    //////////////////////////////////////////////////
    // Post Mappings
    //////////////////////////////////////////////////

    // TODO Change up
    // Unprivileged Operation
    @Operation(summary = "Search for a files by its user, its course, or both")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of files from the search",
                    content = {
                            @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = fileSearch_Output))
                    }
            )
    })
    @PostMapping(path="/file/search", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<SysFile> searchFile(
            @RequestPart @Parameter(description = "Search parameters", required = true) FileStubs.Search stub
    ){
        List<SysFile> files = null;
        Course course;
        User user;

        Integer courseID = stub.getCourseID();
        Integer userID = stub.getUserID();

        // courseJson exists and userJson exists: Search by both class and user
        // TODO finish
        if(!(courseID == null) && !(userID == null)){
            try{
                Optional<Course> opCourse = courseRepository.findById(courseID);
                Optional<User> opUser = userRepository.findById(userID);
                if(opCourse.isEmpty() || opUser.isEmpty()){
                    throw new Exception();
                }
                course = opCourse.get();
                user = opUser.get();
                files = sysFileRepository.findAllByUserAndCourse(user, course);
            } catch(Exception e){
                return null;
            }
        }
        // courseJson exists: Search by class
        else if(!(courseID == null)){
            try{
                Optional<Course> opCourse = courseRepository.findById(courseID);
                if(opCourse.isEmpty()){
                    throw new Exception();
                }
                course = opCourse.get();
                files = sysFileRepository.findAllByCourse(course);
            } catch(Exception e){
                return null;
            }
        }
        // userJson exists: Search by user
        else if(!(userID == null)){
            try{
                Optional<User> opUser = userRepository.findById(userID);
                if(opUser.isEmpty()){
                    throw new Exception();
                }
                user = opUser.get();
                files = sysFileRepository.findAllByUser(user);
            } catch(Exception e){
                return null;
            }
        }
        // Neither courseJson or userJson exists: return nothing
        else {
            files = null;
        }

        return files;
    }

    // Unprivileged Operation
    @Operation(summary = "Uploads a file from a user to a class")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Status of the New")})
    @PostMapping(path="/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFile(
            @RequestPart @Parameter(description = "File to upload", required = true) MultipartFile file,
            @RequestPart @Parameter(description = "New metadata") FileStubs.New stub
    ){

        Course course;
        User owner;
        SysFile sysFile;
        String filePath;
        String fileName;

        fileName = stub.getFileName();

        try{
            Optional<Course> opCourse = courseRepository.findById(stub.getCourseID());
            if(opCourse.isEmpty()){
                throw new Exception();
            }
            course = opCourse.get();
        } catch(Exception e){
            return "Could not find course";
        }

        try{
            Optional<User> opUser = userRepository.findById(stub.getUserID());
            if(opUser.isEmpty()){
                throw new Exception();
            }
            owner = opUser.get();
        } catch(Exception e){
            return "Could not find owner";
        }

        try{
            List<SysFile> files = sysFileRepository.findAllByUserAndCourse(owner, course);
            for(SysFile testFile : files){
                if(testFile.getFileName().equals(fileName)){ throw new Exception(); }
            }
        } catch (Exception e){
            return "SysFile record already exists. Delete or update the old one.";
        }

        try{
            String directoryPath = rootPath
                    + divider + course.getId()
                    + divider + owner.getId() + divider;
            filePath = directoryPath + fileName; // rootPath/ClassID/UserID/fileName
            FileUtils.createFile(directoryPath, filePath, file);
        } catch(Exception e){
            return e.getMessage();
        }

        // Add relationship between file and class in SQL
        sysFile = new SysFile(fileName, filePath, course, owner);
        course.addSysFile(sysFile);
        sysFileRepository.save(sysFile);

        sysFileRepository.flush();
        courseRepository.flush();
        userRepository.flush();

        return "\"id\": " + sysFile.getId();
    }

    //////////////////////////////////////////////////
    // Put Mappings
    //////////////////////////////////////////////////

    // Privileged Operation: Only owners
    @Operation(summary = "Update the name of a file")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Status of the Update")})
    @PutMapping(path="/file/{FileID}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updateFile(
            @PathVariable int FileID,
            @RequestPart @Parameter(description = "New filename", required = true) FileStubs.Update stub,
            @RequestPart @Parameter(description = "User credentials", required = true) UserStubs.Credentials cred
    ){
        // Find the file.
        if(sysFileRepository.findById(FileID).isEmpty()){
            return "Couldn't find file";
        }
        SysFile sysFile = sysFileRepository.findById(FileID).get();

        // Verify user
        try{ UserUtils.verifyFileOwner(cred, sysFile, userRepository); }
        catch(Exception e){ return e.getMessage(); }

        // Grab filename
        String newName = stub.getFileName();

        // Grab old file path and the old file
        String oldFilePath = sysFile.getFilePath();
        File oldFile = new File(oldFilePath);

        // Create old and new File path
        String[] oldFilePathFields = oldFilePath.split(divider);
        int numFields = oldFilePathFields.length;
        String newFilePath = "";
        for(int i = 0; i<numFields-1; i++){
            String nextPathField = oldFilePathFields[i];
            if(!nextPathField.isEmpty()){
                newFilePath += divider;
                newFilePath += nextPathField;
            }
        }
        newFilePath += divider;
        newFilePath += newName;

        // TODO Break this try-catch statement up
        // TODO Delete or report sysfile which cannot be located
        // Move file from old file location to new file location
        try{
            FileInputStream fileReader = new FileInputStream(oldFilePath);
            FileOutputStream fileWriter = new FileOutputStream(newFilePath);
            fileWriter.write(fileReader.readAllBytes());
            oldFile.delete();
        } catch(Exception e){
            return "Failed to replace file";
        }

        sysFile.setFilePath(newFilePath);
        sysFile.setFileName(newName);
        sysFileRepository.save(sysFile);

        return "Success";
    }

    //////////////////////////////////////////////////
    // Del Mappings
    //////////////////////////////////////////////////

    // Privileged Operation: Only owners
    @Operation(summary = "Delete a file by its ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Status of the Delete")})
    @DeleteMapping(path="/file/{FileID}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String deleteFile(
            @PathVariable int FileID,
            @RequestPart @Parameter(description = "User credentials", required = true) UserStubs.Credentials cred
    ){

        // Find the file.
        if(sysFileRepository.findById(FileID).isEmpty()){
            return "Couldn't find file";
        }
        SysFile sysFile = sysFileRepository.findById(FileID).get();

        // Verify user
        try{ UserUtils.verifyFileOwner(cred, sysFile, userRepository); }
        catch(Exception e){ return e.getMessage(); }

        // Grab the file from the filesystem
        String filePath = sysFile.getFilePath();
        File file = new File(filePath);

        // Check for filesystem existence
        if(!file.exists()){
            FileUtils.deleteSysFile(sysFile, sysFileRepository);
            return "File didn't exist; deleted SysFile entry";
        }

        // Delete the file
        if(file.delete()){
            // TODO Worry about the annotations/fields
            FileUtils.deleteSysFile(sysFile, sysFileRepository);
        } else{
            return "Failed to delete file!";
        }

        return "Successfully deleted file";

    }

    //////////////////////////////////////////////////
    // Other Methods
    //////////////////////////////////////////////////



}
