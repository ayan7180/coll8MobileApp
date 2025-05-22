package backend.Courses;

import backend.Users.User;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Archive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String filePath;
    private String abbreviation;
    private int courseNum;

    private Timestamp creationDate;

    @ManyToMany(mappedBy = "archives")
    Set<User> owners;

    public Archive(){
        owners = new HashSet<>();
        creationDate = new Timestamp(System.currentTimeMillis());
    }
    
    public int getId(){
        return id;
    }
    
    public String getFilePath(){
        return filePath;
    }
    public void setFilePath(String filePath){
        this.filePath = filePath;
    }
    
    public String getAbbreviation(){
        return abbreviation;
    }
    public void setAbbreviation(String abbreviation){
        this.abbreviation = abbreviation;
    }

    public int getCourseNum(){
        return courseNum;
    }
    public void setCourseNum(int courseNum){
        this.courseNum = courseNum;
    }

    public Timestamp getCreationDate(){
        return creationDate;
    }

    public Set<User> getOwners() {
        return owners;
    }
    public void addOwner(User owner){
        owners.add(owner);
    }
    public void removeOwner(User owner){
        owners.remove(owner);
    }
}
