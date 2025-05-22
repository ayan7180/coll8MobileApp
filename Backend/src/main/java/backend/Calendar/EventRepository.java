package backend.Calendar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {

    //save to db
    Event save(Event event);
    Event save(Optional<Event> event);
    //findby
    Optional<Event> findById(int id);
    //deleteby
    @Transactional
    void deleteById(int id);
    //findall
    List<Event> findAll();

}
