package backend.Calendar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CalendarRepository extends JpaRepository<Calendar, Integer> {

    Calendar save(Calendar calendar);

    // TODO Soma, test your methods before you push to the main branch. This breaks the code.
    //List<Event> listAllEvents();
    @Query("SELECT c FROM Calendar c")
    List<Calendar> findAll();

    @Query("SELECT e FROM Event e")
    List<Event> findAllEvents();
}
