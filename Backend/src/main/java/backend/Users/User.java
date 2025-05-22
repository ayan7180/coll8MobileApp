package backend.Users;

import backend.Announcements.Announcement;
import backend.Courses.Archive;
import backend.Courses.Course;
import backend.Files.SysFile;
import ch.qos.logback.core.rolling.helper.TimeBasedArchiveRemover;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import backend.Profile.Profile;

import java.util.*;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.arch.Processor;

/**
 * @author Soma Germano
 * this class handles the construction of an instance of user class
 * this class represents a user of the app using fields like username, emailid, etc.
 */
@Entity
@Table(name = "appUser")
public class User {

    /**
     * private fields of an instance of the User class
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String emailId;
    // private boolean isAdmin;
    // private boolean isStudent;
    private String password;
    private String profileImage;
    /**
     * onetoone relationship between a user instance and a profile object
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profileId")
    private Profile profile;


//    @ManyToMany
//    @JsonIgnore
//    @JoinTable(name = "usersFiles_edit", joinColumns = @JoinColumn(name = "userID"), inverseJoinColumns = @JoinColumn(name = "fileID"))
//    private List<SysFile> files_editAccess;

    // TODO Make a set
    @OneToMany // TODO Enable Cascade?
    @JsonIgnore
    private List<SysFile> files_owner;

    // Archived courses that this user had previously owned
    @ManyToMany
    @JsonIgnore
    @JoinTable(name = "archiveOwnership", joinColumns = @JoinColumn(name = "userID"), inverseJoinColumns = @JoinColumn(name = "archiveID"))
    private Set<Archive> archives;

    // Joined courses
    @ManyToMany
    @JsonIgnore
    @JoinTable(name = "courseMembership", joinColumns = @JoinColumn(name = "userID"), inverseJoinColumns = @JoinColumn(name = "courseID"))
    private Set<Course> courses;

    // TODO Make methods
    // Owned Courses
    @ManyToMany
    @JsonIgnore
    @JoinTable(name = "courseOwnership", joinColumns = @JoinColumn(name = "userID"), inverseJoinColumns = @JoinColumn(name = "courseID"))
    private Set<Course> ownedCourses;

    // TODO Delete?
    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private SysFile showcaseFile;

    // Library Preferences
    private int numAnnoucements;
    private int numFiles;

    // TODO Owned courses?

    // Superuser status
    private boolean isSuperuser;

    //constructor for user
    /**
     * creates a new instance of the User class
     * @param username The username of the new user
     * @param password The password of the new user
     * @param id The id of the new user (//TODO Why do we need this?)
     * @param email The email of the new user
     * @param pfp The profile picture of the new user (as a string?)
     */
    public User(String username, String password, int id, String email, String pfp) {
        courses = new HashSet<>();
        ownedCourses = new HashSet<>();
        archives = new HashSet<>();
        files_owner = new ArrayList<>();

        this.username = username;
        this.emailId = email;
        this.password = password;
        this.id = id;
        this.profileImage = pfp;
    }

    /**
     * default constructor
     */
    public User(){
        courses = new HashSet<>();
        ownedCourses = new HashSet<>();
        archives = new HashSet<>();
        files_owner = new ArrayList<>();
    }

    /**
     * retrieves id of the user
     * @return user's id
     */
    public int getId() {
        return id;
    }

    /**
     * updates the id of the user
     * @param id
     */
    public void setId(int id) {this.id= id; }

    /**
     * retrieves the username of the app user
     * @return username
     */
    public String getUsername() { return username; }
    //public void setUsername(String username) { this.username = username; }
    public void setUsername(String username) { this.username = username; }


    /**
     * retrieves the user's email
     * @return emailId
     */
    public String getEmailId() { return emailId; }

    /**
     * updates the user's email
     * @param emailId
     */
    public void setEmailId(String emailId) { this.emailId = emailId; }

    /**
     * retrieves the user's password
     * @return
     */
    public String getPassword() { return password; }

    /**
     * updates the user's password
     * @param password
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * retrieves the filename of the user's profile picture
     * @return profileImage
     */
    public String getProfileImage(){ return profileImage; }

    /**
     * updates the filename of the user's profile picture
     * @param profileImage
     */
    public void setProfileImage(String profileImage){ this.profileImage = profileImage; }

    public Set<Course> getCourses() {
        return courses;
    }
    public void addCourse(Course course){
        courses.add(course);
    }
    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }
    //@Transactional // TODO Is this necessary?
    public void removeCourse(Course course){
        courses.remove(course);
        course.getOwners().remove(this);
    }

    public void setLibraryPreferences(int numAnnouncements, int numFiles) {
        this.numAnnoucements = numAnnouncements;
        this.numFiles = numFiles;
    }

    public int getNumAnnoucements(){
        return numAnnoucements;
    }

    public int getNumFiles(){
        return numFiles;
    }

    public List<SysFile> getFiles_owner() {
        return files_owner;
    }

//    public List<SysFile> getFiles_editAccess() {
//        return files_editAccess;
//    }

    public void setShowcaseFile(SysFile showcaseFile) {
        this.showcaseFile = showcaseFile;
    }

    public SysFile getShowcaseFile() {
        return showcaseFile;
    }
    /**
     * returns the user's profile (an object)
     * @return profile
     */
    public Profile getProfile(){
        return profile;
    }

    /**
     * updates the user's profile object
     * @param profile
     */
    public void setProfile(Profile profile){ this.profile = profile; }

    public boolean isSuperuser() {
        return isSuperuser;
    }

    public void setSuperuser(boolean isSuperuser){
        this.isSuperuser = isSuperuser;
    }

    public Set<Course> getOwnedCourses() {
        return ownedCourses;
    }
    public void addOwnedCourse(Course course){
        ownedCourses.add(course);
    }
    public void setOwnedCourses(Set<Course> ownedCourses) {
        this.ownedCourses = ownedCourses;
    }
    public void removeOwnedCourse(Course course){ ownedCourses.remove(course); }

    public Set<Archive> getArchives() {
        return archives;
    }
    public void addArchive(Archive archive){
        archives.add(archive);
    }
    public void removeArchive(Archive archive){
        archives.remove(archive);
    }

}
