package backend.Files;

import backend.Annotations.Annotation;
import backend.Announcements.Announcement;
import backend.Courses.Course;
import backend.Users.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Entity
public class SysFile implements Comparable<SysFile>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String filePath;
    private String fileName;
    private Timestamp creationDate;
    //TODO lastModification never updated
    private Timestamp lastModification;
    private String fileType;

    @ManyToOne
    @JoinColumn(name = "courseID")
    private Course course;

//    // TODO Not implemented
//    @ManyToMany(mappedBy = "files_editAccess")
//    @JsonIgnore
//    private Set<User> users_editAccess;

    @ManyToOne
    @JoinColumn(name = "ownerID")
    private User owner;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "references")
    // Annotation referencing this
    @JsonIgnore
    private Set<Annotation> AnnRef;

    // Annotations made on this file.
    @OneToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Annotation> AnnChildren;

    @OneToOne
    @JsonIgnore
    private User showcaseOwner;

    public SysFile(String fileName, String filePath, Course course, User owner){
        this.fileName = fileName;
        this.filePath = filePath;
        this.course = course;
        this.owner = owner;
        creationDate = new Timestamp(System.currentTimeMillis());
        lastModification = new Timestamp(creationDate.getTime());
    }

    public SysFile(){
//        this.filePath = null;
//        this.creationDate = null;
//        this.lastModification = null;
//        this.course = null;
            creationDate = new Timestamp(System.currentTimeMillis());
            lastModification = new Timestamp(creationDate.getTime());
    }

    public int getId(){ return id; }

    public void setFilePath(String filePath){ this.filePath = filePath; }
    public String getFilePath(){ return this.filePath; }

    public void setCreationDate(Timestamp creationDate){ this.creationDate = creationDate; }
    public Timestamp getCreationDate(){ return this.creationDate; }

    public void setLastModification(Timestamp lastModification){ this.lastModification = lastModification; }
    public Timestamp getLastModification(){ return this.lastModification; }

    public void setCourse(Course course){ this.course = course; }
    public Course getCourse(){ return this.course; }

    public void setOwner(User owner){ this.owner = owner; }
    public User getOwner() { return this.owner; }

//    public void addEditor(User editor){ this.users_editAccess.add(editor); }
//    public Set<User> getEditors() { return this.users_editAccess; }

    public String getFileType(){ return fileType; }
    public void setFileType(String fileType){ this.fileType = fileType; }

    public String getFileName(){ return fileName; }
    public void setFileName(String fileName){ this.fileName = fileName; }

    public Set<Annotation> getAnnChildren() { return AnnChildren; }
    public void setAnnChildren(Set<Annotation> annChildren) { AnnChildren = annChildren; }
    public void addAnnChild(Annotation ann) { AnnChildren.add(ann); }
    public void removeAnnChild(Annotation ann) { AnnChildren.remove(ann); }

    public Set<Annotation> getAnnRef() { return AnnRef; }
    public void setAnnRef(Set<Annotation> annRef) { AnnRef = annRef; }
    public void addAnnRef(Annotation ann) { AnnRef.add(ann); }
    public void removeAnnRef(Annotation ann) { AnnRef.remove(ann); }

    public void setShowcaseOwner(User showcaseOwner) {
        this.showcaseOwner = showcaseOwner;
    }

    // TODO References
    // TODO Annotations

    @Override
    public int compareTo(SysFile o) {
        return this.lastModification.compareTo(o.lastModification);
    }

}
