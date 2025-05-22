package backend.Courses;

import backend.Stubs;
import backend.Stubs.*;
import backend.Annotations.*;
import backend.Announcements.*;
import backend.Files.*;
import backend.Majors.*;
import backend.Users.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


// TODO Request mapping annotations
@RestController
@Tag(name = "Course Controller", description = "Handles operations for Courses.")
public class CourseController {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    MajorRepository majorRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArchiveRepository archiveRepository;

    @Autowired
    SysFileRepository sysFileRepository;

    @Autowired
    AnnotationRepository annotationRepository;

    @Autowired
    AnnouncementRepository announcementRepository;

    private final String rootArchivePath = "/tmp/coll8/Archives";
    private final String divider = "/";

    // Privileged Operation: Only owners
    @Operation(summary = "Delete a course by its ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Status of the Delete, ID of new archive if successful")})
    @PostMapping(path = "/course/delete/{CourseID}/{username}/{password}")
    String deleteCourse(
            @PathVariable int CourseID,
            @PathVariable String username,
            @PathVariable String password
//            @RequestBody UserStubs.Credentials cred
    ){
        Course course;

        // Find course
        Optional<Course> opCourse = courseRepository.findById(CourseID);
        if(opCourse.isEmpty()){
            return "Failed to find course";
        }
        course = opCourse.get();

        UserStubs.Credentials cred = new UserStubs.Credentials();
        cred.setPassword(password);
        cred.setUsername(username);

        // Verify Owner
        try{
            if(!course.getOwners().isEmpty()) {
                UserUtils.verifyCourseOwner(cred, course, userRepository);
            }
        } catch (Exception e) {
            return e.getMessage();
        }

        // Look for any existing archives that have the same abbreviation and major
        List<Archive> existingArchives = archiveRepository.findByNumAndAbb(course.getCourseNum(), course.getMajor().getAbbreviation());
        for(Archive exArch : existingArchives){
            deleteArchive(exArch.getId());
        }

        try {
            // TODO Make archive

            // TODO Declare fileOutput, objectOutput, and zipOutput streams


            // Whats stopping me from just serializing then entire course class ? Do I need to serialize all the other members along side it?

            // Get all important information from the archive
            Set<User> owners = course.getOwners();
            Set<User> users = course.getUsers();
            Set<Announcement> announcements = course.getAnnouncements();
            List<SysFile> sysFiles = course.getFiles();
            Major major = course.getMajor();

            String zipFilePath = rootArchivePath + divider
                    + course.getMajor().getAbbreviation() + course.getCourseNum() + ".zip";

            File directoryFile = new File(rootArchivePath);
            if(!directoryFile.exists()){
                if(!directoryFile.mkdirs()){
                    throw new Exception("Could not create directories for this archive");
                }
            }

            FileOutputStream fileOutStr = new FileOutputStream(zipFilePath);
            ZipOutputStream zipOutStr = new ZipOutputStream(fileOutStr);
            ObjectOutputStream objOutStr;

            // Copy all course files (build ZIP file)
            for(SysFile file : sysFiles){

                String fileSubdir = divider + file.getOwner().getId() + divider + file.getFileName();
                ZipEntry entry;

                // Create the file entry
                entry = new ZipEntry(fileSubdir + divider + file.getFileName());
                zipOutStr.putNextEntry(entry);

                FileInputStream fileIn = new FileInputStream(file.getFilePath());
                zipOutStr.write(fileIn.readAllBytes());

                zipOutStr.closeEntry();

                File physFile = new File(file.getFilePath());
                physFile.delete();

                // Write file object too?

                Set<Annotation> annChildren = file.getAnnChildren();
                for(Annotation ann : annChildren){
                    entry = new ZipEntry(fileSubdir + divider + "Annotations" + divider + ann.getId());
                    zipOutStr.putNextEntry(entry);

                    objOutStr = new ObjectOutputStream(zipOutStr);
                    objOutStr.writeObject(ann);
                    objOutStr.flush();

                    zipOutStr.closeEntry();
                    // TODO What to do with annotation's children?
                    annotationRepository.delete(ann);
                    courseRepository.save(course);
                }

            }

            List<SysFile> filesToDelete = new ArrayList<>();

            for(SysFile file : sysFiles){
                filesToDelete.add(file);
            }

            for(SysFile file : filesToDelete){
                file.setCourse(null);
                course.removeFile(file);
                sysFileRepository.delete(file);
                courseRepository.save(course);
                sysFileRepository.flush();
            }

            // TODO Do this for all objects?
            List<SysFile> repoFiles = sysFileRepository.findAllByCourse(course);
            for(SysFile file : repoFiles){
                file.setCourse(null);
                course.removeFile(file);
                sysFileRepository.delete(file);
                courseRepository.save(course);
                sysFileRepository.flush();
            }



            Archive archive = new Archive();
            archive.setAbbreviation(course.getMajor().getAbbreviation());
            archive.setCourseNum(course.getCourseNum());
            archive.setFilePath(zipFilePath);

            zipOutStr.close();
            fileOutStr.close();

            archiveRepository.save(archive);
            archiveRepository.flush();


            // Disassociate all owners
            for(User owner : owners){
                owner.getOwnedCourses().remove(course);
                course.getOwners().remove(owner);

                owner.addArchive(archive);
                userRepository.save(owner);
            }

            // Disassociate all users
            for(User user : users){
                user.getCourses().remove(course);
                course.getUsers().remove(user);
                userRepository.save(user);
            }



            // Delete announcements
            for(Announcement announcement : announcements){
                announcement.setCourse(null);
                announcementRepository.delete(announcement);
            }



            // Serialize course identifiers (major, course num, title, etc.)
            major.removeCourse(course);

            courseRepository.save(course);
            courseRepository.flush();
            courseRepository.delete(course);
            courseRepository.flush();

            return "{\"id\": " + archive.getId() + "}";

        } catch(Exception e){
            return "Improper delete request.";
        }
    }


    //////////////////////////////////////////////////
    // Get Mappings
    //////////////////////////////////////////////////

    // Unprivileged Operation
    @Operation(summary = "Retrieve all existing courses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "A list of all courses",
                    content = {
                            @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{ Course, Course, Course, ... }"))
                    }
            )
    })
    @GetMapping(path = "/course")
    List<Course> getCourses(){
        return courseRepository.findAll();
    }

    // Unprivileged Operation
    @Operation(summary = "Retrieve a course by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The course that has been found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Course.class)
                    )
            )
    })
    @GetMapping(path = "/course/search/{CourseID}")
    public Course getCourseByID(
            @PathVariable int CourseID
    ){
        if(courseRepository.findById(CourseID).isEmpty()){
            return null;
        }
        return courseRepository.findById(CourseID).get();
    }

    //////////////////////////////////////////////////
    // Post Mappings
    //////////////////////////////////////////////////

    // Unprivileged Operation
    @Operation(summary = "Retrieve a course by its course number and major's abbreviation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The course that has been found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Course.class)
                    )
            )
    })
    @PostMapping(path = "/course/search")
    public Course getCourse(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Course num and abbreviation", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CourseStubs.Search.class)
                    )
            )
            @RequestBody CourseStubs.Search stub
    ) {
        int courseNum = stub.getCourseNum();
        String abbreviation = stub.getAbbreviation();

        try {
            if( majorRepository.findById(abbreviation).isEmpty() ){
                return null;
            }
            Major major = majorRepository.findById(abbreviation).get();
            Course course = courseRepository.findByNumAndMajor(courseNum, major);
            return course;
        } catch(Exception e){
            return null;
        }
    }

    // Unprivileged Operation
    // Logged operation: Registers a user as the first owner of the new class
    @Operation(summary = "Create a new course for a user")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Status of the upload")})
    @PostMapping(path = "/course", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String addCourse(
            @RequestPart("cred") @Parameter(description = "User credentials", required = true) UserStubs.Credentials cred,
            @RequestPart("stub") @Parameter(description = "Course stub", required = true) CourseStubs.New stub
    ){
        User user;

        // Verify and retrieve User
        try{
            if(!UserUtils.verifyUserByStub(cred, userRepository)) throw new Exception("Incorrect User Credentials");
        } catch (Exception e) {
            return e.getMessage();
        }

        // TODO Running these lines while verifyUserByStub already runs them feel bad. Maybe streamline.
        // Get the user
        Optional<User> opUser =  userRepository.findByUsername(cred.getUsername());
        if(opUser.isEmpty()){
            return "User can't be found.";
        }
        user = opUser.get();

        Course course;
        int courseNum = stub.getCourseNum();
        String title = stub.getTitle();
        String abbreviation = stub.getAbbreviation();

        try {
            if( majorRepository.findById(abbreviation).isEmpty() ){
                return "Couldn't find major";
            }
            Major major = majorRepository.findById(abbreviation).get();
            if( courseRepository.findByNumAndMajor(courseNum, major) != null) {
                return "Class already exists";
            }
            course = new Course();
            course.setCourseNum(courseNum);
            course.setTitle(title);
            course.setMajor(major);

            // TODO Ask if you need to use .save() to save changes to the database

            course.setMajor(major);
            course.addOwner(user);
            course.addUser(user);
            course.addOwner(user);
            courseRepository.save(course);

            major.addCourse(course);
            majorRepository.save(major);

            user.addOwnedCourse(course);
            user.addCourse(course);
            userRepository.save(user);

            return "\"id\": " + course.getId();
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


    //////////////////////////////////////////////////
    // Other Methods
    //////////////////////////////////////////////////

    // TODO CRUDL on course archives

    // Unprivileged action
    @Operation(summary = "Retrieve an archive ZIP file by its ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Archive ZIP file")})
    @GetMapping(path = "/archive/file/{ArchiveID}")
    public Resource getCourseArchive(
            @PathVariable int ArchiveID
    ){
        archiveRepository.flush();
        userRepository.flush();
        courseRepository.flush();

        Optional<Archive> opArchive = archiveRepository.findById(ArchiveID);

        // Archive couldn't be found; return nothing.
        if(opArchive.isEmpty()){
            return null;
        }
        Archive archive = opArchive.get();

        try {
            String filePath = archive.getFilePath();
            File file = new File(filePath);
            FileInputStream fileReader = new FileInputStream(file);

            return new ByteArrayResource(fileReader.readAllBytes());
        } catch(Exception e){
            return null;
        }
    }

    // Unprivileged action
    @Operation(summary = "Return all archive entries that a user owns")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Archive list")})
    @GetMapping(path = "/archive/all/{UserID}")
    public Set<Archive> getUserArchives(
            @PathVariable int UserID
    ){
        userRepository.flush();
        archiveRepository.flush();
        Optional<User> opUser = userRepository.findById(UserID);
        if(opUser.isEmpty()){
            return null;
        }
        User user = opUser.get();

        return user.getArchives();

    }

    // Delete an archive without restoring the course
    @Operation(summary = "Restore a course from this archive file, which deletes this archive file")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Status of the delete")})
    @DeleteMapping(path = "/archive/delete/{ArchiveID}")
    public String deleteArchive(
            @PathVariable int ArchiveID
    ){
        Optional<Archive> opArchive = archiveRepository.findById(ArchiveID);
        if(opArchive.isEmpty()){
            return "Could not find archive file";
        }
        Archive archive = opArchive.get();

        File archiveFile = new File(archive.getFilePath());
        if(archiveFile.exists()){
            archiveFile.delete();
        }

        Set<User> owners = archive.getOwners();
        for(User owner : owners){
            owner.removeArchive(archive);
            archive.removeOwner(owner);
        }
        userRepository.flush();

        archiveRepository.delete(archive);
        return "delete successful";
    }

    // Restore course --> Delete archive
    @Operation(summary = "Restore a course from this archive file, which deletes this archive file")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Status of the delete")})
    @DeleteMapping(path = "/archive/restore/{ArchiveID}")
    public String restoreArchive(
            @PathVariable int ArchiveID
    ){
        courseRepository.flush();
        archiveRepository.flush();
        majorRepository.flush();
        userRepository.flush();

        Optional<Archive> opArchive = archiveRepository.findById(ArchiveID);
        if(opArchive.isEmpty()){
            return "Could not find archive file";
        }
        Archive archive = opArchive.get();

        int courseNum = archive.getCourseNum();
        String abbreviation = archive.getAbbreviation();

        Optional<Major> opMajor = majorRepository.findById(abbreviation);
        if(opMajor.isEmpty()){
            return "Could not find the major this course archive is associated with: " + abbreviation;
        }
        Major major = opMajor.get();

        Course course = courseRepository.findByNumAndMajor(courseNum, abbreviation);
        if(!(course == null)){
            return "Course with course number (" + courseNum + ") under major (" + abbreviation + ") already exists";
        }

        course = new Course();
        course.setMajor(major);

        // TODO read ZIP file
        FileInputStream fileIn;
        ZipInputStream zipIn;
        ObjectInputStream objIn;
        try{
//            fileIn = new FileInputStream(archive.getFilePath());
//            zipIn = new ZipInputStream(fileIn);

        }catch (Exception e){
            deleteArchive(ArchiveID);
            return "Failed to find archive file; deleted archive stub";
        }

        // Create Files and Assign File Owners, add said owners to course
        try{
            ZipFile zipFile = new ZipFile(archive.getFilePath());
            Enumeration<? extends ZipEntry> entries =  zipFile.entries();
            while(entries.hasMoreElements()){

                ZipEntry entry = entries.nextElement();

                String entryPath = entry.getName();
                String[] pathPartsArr = entryPath.split("/");
                String[] badPartsArr = {"", null};
                List<String> pathParts = new ArrayList<>(Arrays.asList(pathPartsArr));
                List<String> badParts = new ArrayList<>(Arrays.asList(badPartsArr));
                pathParts.removeAll(badParts);

                int UserID = Integer.parseInt(pathParts.get(0));
                Optional<User> opOwner = userRepository.findById(UserID);
                if(opOwner.isEmpty()){
                    continue;
                }
                User owner = opOwner.get();
                course.addUser(owner);
                owner.addCourse(course);

                String fileName = pathParts.get(2);

                File file;

                String directoryPath = FileController.rootPath
                        + divider + course.getId()
                        + divider + owner.getId() + divider;
                File directoryFile = new File(directoryPath);
                if(!directoryFile.exists()){
                    directoryFile.mkdirs();
                }

                // Create the file
                try{
                    file = new File(directoryPath + fileName);
                    InputStream inStream = zipFile.getInputStream(entry);
                    FileOutputStream fileOut = new FileOutputStream(file);
                    fileOut.write(inStream.readAllBytes());

                } catch(Exception e){
                    continue; // TODO Should something else be done here?
                }

                // Create the sysfile entry
                SysFile sysFile = new SysFile();
                sysFile.setCourse(course);
                sysFile.setOwner(owner);
                sysFile.setFilePath(directoryPath + fileName);
                sysFile.setFileName(fileName);

                courseRepository.save(course);
                userRepository.save(owner);
                sysFileRepository.save(sysFile);
                sysFileRepository.flush();

                // Do I need to add the owner manually here? I haven't had to in the past...

                // TODO Read entry, create user, create file under user and course
                // Only returns the directory /user/file, and doesn't return /user/file/filename, which also exists

            }
        }catch (Exception e){
            return "Failed to read ZIP file structure";
        }

        // Assign course owners
        Set<User> owners = archive.getOwners();
        for(User courseOwner : owners){
            course.addOwner(courseOwner);
            course.addUser(courseOwner);
            courseOwner.addOwnedCourse(course);
            userRepository.save(courseOwner);
            userRepository.flush();
        }

        deleteArchive(ArchiveID);

        courseRepository.save(course);

        return "{\"id\": " + course.getId() + "}";
    }




}
