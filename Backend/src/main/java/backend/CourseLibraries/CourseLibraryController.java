package backend.CourseLibraries;

import backend.Announcements.AnnouncementRepository;
import backend.Courses.Course;
import backend.Courses.CourseRepository;
import backend.Files.SysFileRepository;
import backend.Users.User;
import backend.Users.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@Tag(name = "Course Library Controller", description = "Handles course library operations.")
public class CourseLibraryController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    AnnouncementRepository announcementRepository;

    @Autowired
    SysFileRepository sysFileRepository;

    @Operation(summary = "Get course library for a particular user")

    @GetMapping(path = "/library/{username}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "A list of course thumbnails",
                    content = {
                            @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{ Thumbnail, Thumbnail, Thumbnail, ... }"))
                    }
            )
    })
    public List<CourseThumbnail> getUserLibrary(@PathVariable String username){
        List<CourseThumbnail> thumbnails = new ArrayList<>();

        if(userRepository.findByUsername(username).isEmpty()){
            return null;
        }
        User user = userRepository.findByUsername(username).get();
        Set<Course> courses = user.getCourses();

        courses.forEach( (course)->{
            thumbnails.add(LibraryUtils.generateThumbnail(course, user));
        });

        return thumbnails;
    }

    // TODO Displaying the wrong schema for some reason.
    @Operation(summary = "Update a user's course library preferences")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Status of the Delete")})
    @PutMapping(path = "library/updatePreferences")
    public String setLibraryPreferences(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "username, numAnnouncements, numFiles", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CourseLibraryStubs.Update.class)
                    )
            )
            @RequestBody CourseLibraryStubs.Update stub
    ){
        String username = stub.getUsername();
        int numAnnouncements = stub.getNumAnnouncements();
        int numFiles = stub.getNumFiles();

        if(userRepository.findByUsername(username).isEmpty()){
            return "Could not find user!";
        }
        User user = userRepository.findByUsername(username).get();

        user.setLibraryPreferences(numAnnouncements, numFiles);

        userRepository.save(user);

        return "Success!";

    }

    // TODO

}
