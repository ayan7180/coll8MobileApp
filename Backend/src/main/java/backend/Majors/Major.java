package backend.Majors;

import backend.Courses.Course;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Major {

    //////// Private Fields ////////

    @Id
    private String abbreviation;
    private String title;

    @OneToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Course> courses;

    //////// Constructors ////////

    public Major(String abbreviation){
        this.abbreviation = abbreviation;
        courses = new ArrayList<>();
    }
    public Major(String abbreviation, String title){
        this.abbreviation = abbreviation;
        this.title = title;
        courses = new ArrayList<>();
    }

    public Major(){
        courses = new ArrayList<>();
    }

    //////// Getters/Setters ////////

    public String getAbbreviation(){
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation){
        this.abbreviation = abbreviation;
    }

    public void addCourse(Course course){
        this.courses.add(course);
    }

    public void removeCourse(Course course){
        this.courses.remove(course);
    }

    public String getTitle() {
        return title;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Course> getCourses(){
        return this.courses;
    }


}
