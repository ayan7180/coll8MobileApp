package backend.Majors;

import backend.Stubs.*;
import backend.Courses.Course;
import backend.Courses.CourseRepository;
import backend.Users.User;
import backend.Users.UserRepository;
import backend.Users.UserUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "Major Controller", description = "Handles course Major operations.")
public class MajorController {

    

    public final String abbString = "{ \"abbreviation\": String}";
    public final String classListStr = "{ {course}, {course}, ... }";
    public final String majorListStr = "{ {major}, {major}, ... }";

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    MajorRepository majorRepository;

    @Autowired
    UserRepository userRepository;

    //////////////////////////////////////////////////
    // Get Mappings
    //////////////////////////////////////////////////

    // Unprivileged Operation
    @Operation(summary = "Get all majors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returning a list of majors",
                    content = {
                            @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = majorListStr))
                    }
            )
    })
    @GetMapping(path = "/major")
    List<Major> getMajors(){
        return majorRepository.findAll();
    }

    //////////////////////////////////////////////////
    // Post Mappings
    //////////////////////////////////////////////////

    // Unprivileged Operation
    @Operation(summary = "Get all classes under a specific major")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "These classes that have been found",
                    content = {
                            @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = classListStr))
                    }
            )
    })
    @PostMapping(path = "/major/search")
    public List<Course> getClassesFromMajor(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Major abbreviation", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MajorStubs.Search.class)
                    )
            )
            @RequestBody MajorStubs.Search majorStub
    ){
        String abbreviation = majorStub.getAbbreviation();

        try {
            // Try this sometime: findById().getPresent()
            if( majorRepository.findById(abbreviation).isEmpty() ){
                return null;
            }
            Major major = majorRepository.findById(abbreviation).get();
            return major.getCourses();
        } catch(Exception e){
            return null;
        }
    }

    // Unprivileged Operation
    @Operation(summary = "Create a new major")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post completion status",
                    content = { @Content(mediaType = "application/json") }
            )
    })
    @PostMapping(path = "/major", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String addMajor(
            @RequestPart @Parameter(description = "User credentials", required = true) UserStubs.Credentials cred,
            @RequestPart @Parameter(description = "Major stub", required = true) MajorStubs.New stub
    ){
        String abbreviation = stub.getAbbreviation();
        String title = stub.getTitle();

        try {
            if( majorRepository.findById(abbreviation).isPresent() ){
                return "Major already Exists";
            }
            Major major = new Major(abbreviation, title);
            majorRepository.save(major);
            return "\"ID\": " + major.getAbbreviation();
        } catch(Exception e){
            return "Improper post request.";
        }
    }

    //////////////////////////////////////////////////
    // Put Mappings
    //////////////////////////////////////////////////

    //////////////////////////////////////////////////
    // Del Mappings
    //////////////////////////////////////////////////

    // Privileged Operation: Only superusers
    @Operation(summary = "Delete a specific major")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete completion status",
                    content = { @Content(mediaType = "application/json") }
            )
    })
    @DeleteMapping(path = "/major", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String deleteMajor(
            @RequestPart @Parameter(description = "User credentials", required = true) UserStubs.Credentials cred,
            @RequestPart @Parameter(description = "Major stub", required = true) MajorStubs.New stub
    ){

        // Verify User
        try{
            UserUtils.verifySuperuserStatus(cred, userRepository);
        } catch (Exception e) {
            return e.getMessage();
        }

        String abbreviation = stub.getAbbreviation();

        try {
            if( majorRepository.findById(abbreviation).isEmpty() ){
                return "Major not found";
            }
            Major major = majorRepository.findById(abbreviation).get();
            if( !major.getCourses().isEmpty() ){
                return "Classes still exist in this major. Delete them first.";
            }
            majorRepository.delete(major);
            return "Major Removed!";
        } catch(Exception e){
            return "Improper post request.";
        }
    }

    //////////////////////////////////////////////////
    // Other Methods
    //////////////////////////////////////////////////





}
