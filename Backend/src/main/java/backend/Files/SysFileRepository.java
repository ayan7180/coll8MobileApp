package backend.Files;

import backend.Courses.Course;
import backend.Majors.Major;
import backend.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SysFileRepository extends JpaRepository<SysFile, Integer> {
    // TODO How come these methods work for accessing the SQL database without actually overwriting another method ??

    List<SysFile> findAll();

    @Query("SELECT f FROM SysFile f WHERE f.owner = ?1 AND f.course = ?2")
    List<SysFile> findAllByUserAndCourse(User user, Course course);

    @Query("SELECT f FROM SysFile f WHERE f.owner = ?1")
    List<SysFile> findAllByUser(User user);

    @Query("SELECT f FROM SysFile f WHERE f.course = ?1")
    List<SysFile> findAllByCourse(Course course);



}
