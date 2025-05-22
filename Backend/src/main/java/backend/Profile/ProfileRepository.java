package backend.Profile;

import backend.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;  //crudrepo or jparepo
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * @author Soma Germano
 * this class handles the CRUD repository inherited from the JPA repository
 *
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer>{
    /**
     * finds a profile by the username
     * @param name
     * @return
     */
    Optional<Profile> findByName(String name);

    /**
     * finds a profile by id
     * @param id
     * @return
     */
    Optional<Profile> findById(int id);

    /**
     * saves a profile to the database
     * @param profile
     * @return
     */
    Profile save(Profile profile);


    /**
     * lists all profiles
     * @return
     */
    List<Profile> findAll();

    /**
     * deletes a profile by the id
     * @param profileId
     */
    @Transactional
    void deleteById(int profileId);
}
