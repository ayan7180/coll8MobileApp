package coms309.people;

/**
 * @author Soma Germano
 * this file stores student records
 */
public class Student {
    private String firstName;
    private String lastName;
    private int studentId;
    private String major;
    private String classification;

    //initialize variables in constructor
    public Student(String firstName, String lastName, int studentId, String major, String classification) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentId = studentId;
        this.major = major;
        this.classification = classification;
    }

    //getter and setter functions to retrieve and change data:
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getStudentId() { return studentId; }
    public String getMajor() { return major; }
    public String getClassification() { return classification; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public void setMajor(String major) { this.major = major; }
    public void setClassification(String classification) {
        this.classification = classification;
    }

    //print out students information
    @Override
    public String toString() {
        return firstName + " " + lastName + " " + studentId + " " + major + " " + classification;
    }
}
