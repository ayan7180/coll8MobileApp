package backend.Courses;

import backend.Majors.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArchiveRepository extends JpaRepository<Archive, Integer> {

    @Query("SELECT a FROM Archive a WHERE a.courseNum = ?1 AND a.abbreviation = ?2")
    List<Archive> findByNumAndAbb(int courseNum, String abbreviation);

}
