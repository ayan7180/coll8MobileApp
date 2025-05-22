package backend.FileLibrary;
//import statements
import jakarta.persistence.*;

/**
 * @author Soma Germano
 * this class handles the construction of an instance of a filelibrary
 */
@Entity
@Table(name = "file_library")
public class FileLibrary {
    //private fields
    private String fileName; //make this the primary key
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String fileAuthor;

    /**
     * constructor creates an instance of a file library
     * @param fileName
     */
     public FileLibrary(String fileName){
        this.fileName = fileName;
     }
    //getter + setter methods

    /**
     * updates filename
     * @param fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * retrieves filename
     * @return
     */
    public String getFileName(){return fileName;}

    /**
     * retrieves file id
     * @return
     */
    public int getId(){ return id; }

    /**
     * updates fileid
     * @param id
     */
    public void setFileId(int id){ this.id=id; }
}
