package backend.Courses;

import backend.Majors.Major;
import backend.Majors.MajorRepository;
import com.fasterxml.jackson.databind.JsonNode;

public class CourseUtils {

    public static Course getCourse(int courseNum, String abbreviation,
                                   CourseRepository courseRepository, MajorRepository majorRepository)
            throws Exception{

        if( majorRepository.findById(abbreviation).isEmpty() ){
            return null;
        }
        Major major = majorRepository.findById(abbreviation).get();
        Course course = courseRepository.findByNumAndMajor(courseNum, major);
        return course;

    }

    public static Course getCourseByJSON(JsonNode json,
                                         CourseRepository courseRepository, MajorRepository majorRepository)
    throws Exception{

        Course course;

        int courseNum = json.get("courseNum").asInt();
        String abbreviation = json.get("abbreviation").asText();

        course = CourseUtils.getCourse(courseNum, abbreviation, courseRepository, majorRepository);
        if(course == null){ throw new Exception(); }

        return course;
    }

}
