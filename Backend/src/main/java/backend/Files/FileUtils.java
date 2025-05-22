package backend.Files;

import backend.Annotations.Annotation;
import backend.Courses.Course;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Set;

public class FileUtils {

    public static void createFile(String directoryPath, String filePath, MultipartFile file)
            throws IOException {


        File directoryFile = new File(directoryPath);
        if(!directoryFile.exists()){ // Use isDirectory()??
            directoryFile.mkdirs();
        }

        File newFile = new File(filePath);
        if(newFile.exists()){
            // Right now, we just replace the file. If we need to not do that, uncomment the next line which throws an exception.
            //throw new IOException("File already exists; abort write.");
        }
        FileOutputStream fileWriter = new FileOutputStream(newFile);
        fileWriter.write(file.getBytes());
    }

    // TODO Once other references start functioning, this function will have to remove those references too.
    public static void deleteSysFile(SysFile sysFile, SysFileRepository repo){
        Course course = sysFile.getCourse();
//        Set<Annotation> annChildren = sysFile.getAnnChildren();
//        Set<Annotation> annRef = sysFile.getAnnRef();
//        annChildren.forEach(ann -> {
//            ann.deleteReference(sysFile);
//        });
//        annRef.forEach(ann -> {
//           ann.deleteReference();
//        });
        course.removeFile(sysFile);
        repo.delete(sysFile);
    }

    public static JsonNode getJsonFromFile(MultipartFile jsonFile){
        // Grab the File ID
        JsonNode json;
        try{
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.readTree(jsonFile.getBytes());
        }catch(Exception e){
            return null;
        }
        return json;
    }

}
