package backend.Annotations;

import backend.Files.SysFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// TODO Should I be using Longs instead of Integers?
public interface AnnotationRepository extends JpaRepository<Annotation, Integer> {

    // TODO Does a Set work?
    @Query("SELECT a FROM Annotation a WHERE a.parentFile = ?1")
    List<Annotation> findAllByFile(SysFile file);
}
