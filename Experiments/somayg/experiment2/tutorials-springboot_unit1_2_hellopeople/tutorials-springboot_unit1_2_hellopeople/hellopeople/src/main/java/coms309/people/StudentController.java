package coms309.people;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.HashMap;

/**
 * Controller used to showcase Create and Read from a LIST
 *
 * @author Vivek Bengre
 */

@RestController
public class StudentController {


    HashMap<String, Student> studentList = new  HashMap<>();


    @GetMapping("/student")
    public  HashMap<String, Student> getAllPersons() {
        return studentList;
    }


    @PostMapping("/student")
    public  String createStudent(@RequestBody Student student) {
        System.out.println(student);
        studentList.put(student.getFirstName(), student);
        return "New student "+ student.getFirstName() + " Saved";
    }


    @GetMapping("/student/{firstName}")
    public Student getStudent(@PathVariable String firstName) {
        Student s = studentList.get(firstName);
        return s;
    }


    @PutMapping("/student/{firstName}")
    public Student updateStudent(@PathVariable String firstName, @RequestBody Student s) {
        studentList.replace(firstName, s);
        return studentList.get(firstName);
    }


    @DeleteMapping("/student/{firstName}")
    public HashMap<String, Student> deleteStudent(@PathVariable String firstName) {
        studentList.remove(firstName);
        return studentList;
    }
}


