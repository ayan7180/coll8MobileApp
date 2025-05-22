package backend.Announcements;

import backend.Courses.Course;
import jakarta.persistence.*;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
//import jakarta.persistence.Id;

/**
 * @author Soma Germano
 * This class handles the construction of an announcement
 */
@Entity
public class Announcement implements Comparable<Announcement> {

    //private fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String text;
    //TODO lastModification never updated
    private Timestamp creationDate;
    private Timestamp lastModification;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "courseID")
    private Course course;


    /**
     * Creates a new instance of the announcement class
     * @param title
     * @param text
     * @param id
     */
    //FIXME: might need primary key id
    public Announcement(String title, String text, Integer id) {
        this.title = title;
        this.text = text;
        this.id = id;
        creationDate = new Timestamp(System.currentTimeMillis());
        lastModification = new Timestamp(creationDate.getTime());
    }
    public Announcement() {
        creationDate = new Timestamp(System.currentTimeMillis());
        lastModification = new Timestamp(creationDate.getTime());
    }

    //getters and setters

    /**
     * when called upon, this method returns the object's title
     * @return
     */
    public String getTitle() { return title; }

    /**
     *when called upon, this method returns the object's text
     * @return
     */
    public String getText() { return text; }

    /**
     *when called upon, this method returns the object's id
     * @return
     */
    public int getId() { return id; }

    /**
     *when called upon, this method sets the object's title
     * @param title
     */
    public void setTitle(String title) { this.title = title; }

    /**
     *when called upon, this method sets the object's text
     * @param text
     */
    public void setText(String text) { this.text = text; }

    /**
     *when called upon, this method sets the object's id
     * @param id
     */
    public void setId(int id) { this.id = id; }

    public void setCreationDate(Timestamp creationDate){ this.creationDate = creationDate; }
    public Timestamp getCreationDate(){ return this.creationDate; }

    public void setLastModification(Timestamp lastModification){ this.lastModification = lastModification; }
    public Timestamp getLastModification(){ return this.lastModification; }

    public Course getCourse() { return course; }

    public void setCourse(Course course) { this.course = course; }

    @Override
    public int compareTo(Announcement o) {
        return this.lastModification.compareTo(o.lastModification);
    }
}
