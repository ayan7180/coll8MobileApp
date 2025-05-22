package backend.Courses;

import backend.Announcements.Announcement;
import backend.Files.SysFile;
import backend.Majors.Major;
import backend.Users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
//import jakarta.transaction.Transactional;

import java.util.*;

// TODO Ask professor how to drop mass-duplicate tables
// TODO Find out how to do an if-exists to find existing tables and either use them or drop them

@Entity
public class Course {

    //////// Private Fields ////////

    // TODO NOTE: Find a better way to prototype making big changes to tables, like changing its id

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int courseNum;
    private String title;

    @ManyToOne  // DONT USE: (cascade = CascadeType.ALL), it will delete a major whenever you delete a single class, which is undesireable.
    // TODO Is a join column necessary here?
    @JoinColumn(name = "majorAbbreviation")
    private Major major;

    @OneToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SysFile> files;

//    // TODO make many-to-many
//    @ManyToOne
//    @JoinColumn(name = "userID")
//    private User user;

    // TODO Make methods, verify functionality as a many-to-many relationship
    // Members
    @ManyToMany(mappedBy = "courses")
    @JsonIgnore
    private Set<User> users;

    // Owners
    @ManyToMany(mappedBy = "ownedCourses")
    @JsonIgnore
    private Set<User> owners;

    // TODO make a way to add announcements to a course
    @OneToMany(cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<Announcement> announcements;

    //////// Constructors ////////

//    public Course(int courseNum){
//        this.courseNum = courseNum;
//    }

//    public Course(int courseNum, String title, Major major){
//        this.courseNum = courseNum;
//        this.title = title;
//        this.major = major;
//        this.files = new ArrayList<>();
//        this.announcements = new HashSet<>();
//    }

//    public Course(Integer courseNum){
//        this.courseNum = courseNum.intValue();
//    }

    public Course(){
        this.announcements = new HashSet<>();
        this.files = new ArrayList<>();
        this.owners = new HashSet<>();
        this.users = new HashSet<>();
    }

    //////// Getters/Setters ////////

    public int getCourseNum(){
        return courseNum;
    }

    public void setCourseNum(int courseNum){
        this.courseNum = courseNum;
    }

    public void setMajor(Major major){
        this.major = major;
    }

    public Major getMajor(){
        return major;
    }

    public void setTitle(String title) { this.title = title; }

    public String getTitle() { return title; }

    public void addSysFile(SysFile sysFile){
        files.add(sysFile);
    }

    public void removeFile(SysFile sysFile) { files.remove(sysFile); }

    public int getId(){
        return this.id;
    }

    public List<SysFile> getFiles(){
        return this.files;
    }

    public Announcement[] getRecentAnnouncements(int numElements) {
        Announcement[] annArr = new Announcement[numElements];
        ArrayList<Announcement> annList = new ArrayList<>(announcements);
        Collections.sort(annList);
        for(int i = 0; i < numElements; i++){
            if(i < annList.size()){
                annArr[i] = annList.get(i);
            }
            else{
                annArr[i] = null;
            }
        }
        return annArr;
    }

    public SysFile[] getRecentFiles(int numElements) {
        SysFile[] sysArr = new SysFile[numElements];
        ArrayList<SysFile> sysList = new ArrayList<>(files);
        Collections.sort(sysList);
        for(int i = 0; i < numElements; i++){
            if(i < sysList.size()){
                sysArr[i] = sysList.get(i);
            }
            else{
                sysArr[i] = null;
            }
        }
        return sysArr;
    }

    public Set<Announcement> getAnnouncements() {
        return announcements;
    }
    public void setAnnouncements(Set<Announcement> announcements) {
        this.announcements = announcements;
    }
    public void addAnnouncement(Announcement announcement){
        this.announcements.add(announcement);
    }

    public Set<User> getUsers() { return users; }
    public void setUsers (Set<User> users) { this.users = users; }
    public void addUser(User user) { users.add(user); }
    public void removeUser(User user) { users.remove(user); }

    public Set<User> getOwners() { return owners; }
    public void setOwners (Set<User> owners) { this.owners = owners; }
    public void addOwner(User owner) { owners.add(owner); }
//    @Transactional
    public void removeOwner(User owner) {
        users.remove(owner);
        owner.getCourses().remove(this);
    }


}
