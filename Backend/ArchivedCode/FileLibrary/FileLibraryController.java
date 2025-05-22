package backend.FileLibrary;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
//import backend.FileLibrary.FileLibraryRepository;

/**
 * @author Soma Germano
 * this class handles CRUD operations of
 */
@RestController
@Tag(name = "File Library Controller", description = "//TODO Soma")
public class FileLibraryController {
    //fields
    @Autowired
    FileLibraryRepository fileLibraryRepository;
    

    //TO DO : CONNECT TO SIMON'S UPLOADING FILE FEATURE




    /**
     * retrieves file by name in database
     * names are distinct
     * @param fileName
     * @return
     */
    @GetMapping(path = "/filelibrary/{fileName}")
    public ResponseEntity<?> getFileByName(@PathVariable String fileName) {
        Optional<FileLibrary> file = fileLibraryRepository.findByName(fileName);
        if (file.isPresent()) {
            return ResponseEntity.ok(file.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("file not found/cant get file");
        }
    }



    /**
     * updates filename
     * @param id
     * @param changefileName
     * @return
     */
    @PutMapping(path = "/filelibrary/{id}")
    public String updateFileName(@RequestParam int id, @RequestParam String changefileName){
        //check if file existing
        Optional<FileLibrary> existingF = fileLibraryRepository.findById(id);
        if(existingF.isPresent()){
            //change file name
            FileLibrary file = existingF.get();
            file.setFileName(changefileName);

            return "updated file name";
        }else{
            return "file non-existent can't update name";
        }

        //return "";
    }
    //UPDATE - search feature but modified

    //





    /**
     * deletes a file from table file_library
     * @param fileName
     * @param id
     * @return
     */
    @DeleteMapping(path = "/filelibrary/id")
    public String deleteFile(@PathVariable String fileName, Integer id){
        Optional<FileLibrary> existingFile = fileLibraryRepository.findByName(fileName);
        if(existingFile.isPresent()){
            fileLibraryRepository.deleteById(id);
            return "file deleted";
        }else{
            return "file not found";
        }
    }



    /**
     * lists all file in the table file_library
     * @return
     */
    @GetMapping(path = "/filelibrary/all")
    public List<FileLibrary> getAllFiles(){
        return fileLibraryRepository.findAll();
    }


    //PERSONAL FILE REPO:
}
