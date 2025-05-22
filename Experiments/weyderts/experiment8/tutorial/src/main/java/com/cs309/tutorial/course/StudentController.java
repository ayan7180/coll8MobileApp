package com.cs309.tutorial.course;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
//@RequestMapping(value="/student")
@RequestMapping("/student")
public class StudentController {
	
	@Autowired
	StudentRepository studentRepo;
	
	@Autowired
	CourseRepository CourseRepository;
	
	@PostMapping("/create")
	public String createStudent(@RequestBody ObjectNode json) {
		Student student = new Student((long)json.get("id").asInt());
		studentRepo.save(student);
		return "Student created!";
	}

	@GetMapping(path = "")
	public List<Student> getStudents(){
		return studentRepo.findAll();
	}

	@GetMapping("/get")
	public Student getStudent(@RequestParam Long id) {
		return studentRepo.getOne(id);
	}
	
	@PostMapping("/registerCourse")
	public void registerCourse(@RequestBody ObjectNode json) {
		Long studentID = (long)json.get("id").asInt();
		Student student = studentRepo.getReferenceById(studentID);
		student.setRegistration(new CourseRegistration());
		studentRepo.save(student);
	}
}
