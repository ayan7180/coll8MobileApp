package backend.Phones;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Vivek Bengre
 * 
 */ 

public interface PhoneRepository extends JpaRepository<Phone, Long> {
    Phone findById(int id);

    @Transactional
    void deleteById(int id);
}
