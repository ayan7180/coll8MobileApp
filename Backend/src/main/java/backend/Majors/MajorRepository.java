package backend.Majors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MajorRepository extends JpaRepository<Major, String> {
//    // TODO How to make the find method use a string key?? Does this work??

    @Override
    Major getReferenceById(String s);

//
//    // TODO Does this work, and if so, how??
//    @Transactional
//    void deleteByAbbreviation(String string);


}