package backend.Courses;

import backend.Majors.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    // TODO How come these methods work for accessing the SQL database without actually overwriting another method ??

    List<Course> findAll();

    @Query("SELECT c FROM Course c WHERE c.courseNum = ?1 AND c.major = ?2")
    Course findByNumAndMajor(int courseNum, Major major);

    @Query("SELECT c FROM Course c WHERE c.courseNum = ?1 and c.major.abbreviation = ?2")
    Course findByNumAndMajor(int courseNum, String abbreviation);

//    @Query("SELECT * FROM course WHERE courseNum = ?1 AND major_abbreviation = ?2")
//    Course findByNumAndAbbr(int courseNum, String major_abbreviation);

//    @Transactional
//    void deleteById(int id);
//

}
