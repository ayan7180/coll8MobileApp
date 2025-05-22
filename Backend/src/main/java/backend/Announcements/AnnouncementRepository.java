package backend.Announcements;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;  //crudrepo or jparepo
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author Soma Germano
 * This class handles the CRUD operations provided by the JpaRepository Interface
 */
public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {


    /**
     * this method retrieves and instance of announcement by the title
     * @param title
     * @return
     */
    Optional<Announcement> findByTitle(String title);

    /**
     * this method retrieves an instance of announcement by the id
     * @param id
     * @return
     */
    Optional<Announcement> findById(int id);



    /**
     * this method saves an instance of the announcement class in the database
     * overrides the save from the jpa repo
     * @param announcement
     * @return
     */
    Announcement save(Announcement announcement);

    /**
     * this method deletes an instance of the announcement class from the database by id
     * @param id
     */
    @Transactional
    void deleteById(int id);


    /**
     * this method lists/retrieves all instances of the announcement class from the database
     * @return
     */
    List<Announcement> findAll();

}
