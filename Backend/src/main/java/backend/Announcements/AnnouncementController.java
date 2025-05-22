package backend.Announcements;

import backend.Stubs.*;
import backend.Courses.*;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

/**
 * @author Soma Germano
 * This class handles the storage and retreival of
 * our announcements from the database using CRUD operations
 */

@RestController
@Tag(name = "Announcement Controller", description = "Handles CRUDL for announcements, which can be site-wide and course-wide.")
public class AnnouncementController {

    @Autowired
    AnnouncementRepository announcementRepository;

    @Autowired
    CourseRepository courseRepository;

    @Operation(summary = "New an announcement to a course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The file that has been found",
                    content = {
                            @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{ \"id\": int }"))
                    }
            )
    })
    @PostMapping(path = "/announcements/{courseID}")
    public Object createAnnouncement_course(
            @PathVariable int courseID,
            @RequestBody AnnouncementStubs.New stub
    ){
        Announcement ann = new Announcement();
        ann.setTitle(stub.getTitle());
        ann.setText(stub.getText());

        if(courseRepository.findById(courseID).isEmpty()){
            return "Couldn't find course";
        }
        Course course = courseRepository.findById(courseID).get();
        ann.setCourse(course);
        course.addAnnouncement(ann);

        announcementRepository.save(ann);
        courseRepository.save(course);

        return "\"ID\": " + ann.getId();
    }

    //create
    /*
      format from front end:
      String array[] = {"abstract", "body"}


    /**
     * This method takes a string array and creates a new announcement
     * and saves the new announcement into the repository (database)
     * The string array should be organized with the first element as the title and the second element as the text
     * String array[] ={"Exam Prep","A new practice exam has been posted on Canvas for the upcoming exam"}
     * @param a
     * @return
     */
     @PostMapping(path = "/announcements")
     public Object createAnnouncement(@RequestBody String[] a) {
         Announcement a1 = new Announcement();
         if(a.length > 3){
              return "size of array too big";
          }
          else{
             a1.setTitle(a[0]);
             a1.setText(a[1]);
//             int num = Integer.parseInt(a[2]); //not necessary
//             a1.setId(num);
             announcementRepository.save(a1);
             return "success";
          }
          //return ""; //maybe use hashmap instead of directly returning string
     }



    /**
     * This method gets a certain announcement's text
     * @param an
     * @return
     */
     @RequestMapping(path = "/announcements")
     public String getAnnouncement(@RequestParam String an[]) {
         Announcement a1 = new Announcement();
         a1.setTitle(an[0]);
         a1.setText(an[1]);
         return a1.getText();
        // Optional<Announcement> a = announcementRepository.findByTitle(an.text);
     }


    /**
     * This method updates a specific announcement
     * It takes id parameter and searches for the announcement in the repository by the given id
     * If the instance exists, it takes the second parameter newText and updates the instances text field
     * @param id
     * @param newText
     * @return
     */
     @PutMapping(path = "/announcements/{id}")
    public String updateAnnouncement(@PathVariable int id, @RequestBody String newText) {
      Optional<Announcement> existingAn = announcementRepository.findById(id);
      if(existingAn.isPresent()){
          Announcement an1 = existingAn.get();
          //change text to announcement
          an1.setText(newText);
          announcementRepository.save(an1);
          return "updated text";
      }else{
          return "announcement not found";
      }
    }

    /**
     * this method deletes an instance of the announcement class by id, which is given in the parameter
     * @param id
     * @return
     */
    @DeleteMapping(path = "/announcements/{id}")
    public String deleteAnnouncement(@PathVariable int id) {
       Optional<Announcement> existingUser = announcementRepository.findById(id);
       if(existingUser.isPresent()){
           announcementRepository.deleteById(existingUser.get().getId());
           return "announcement deleted";
       }else{
           return "announcement not found";
       }
    }


    /**
     * this method returns all instances of the announcement class saved in the respository(database)
     * @return
     */
    @GetMapping(path = "/announcements/all")
    public List<Announcement> getAllAnnouncements(){
         return announcementRepository.findAll();
    }

}
