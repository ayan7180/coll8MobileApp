package backend.Users;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Soma Germano
 * this interface handles the CRUD operations provided/inherited by the JPA Repository
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    //List<User> findAll();

    /**
     * this method finds a user by its id in the database
     * @param id
     * @return
     */
    Optional<User> findById(int id);
    //@Override

    /**
     * this method saves an instance of the user class in the database
     * @param user
     * @return
     */
    User save(User user);

    /**
     * this method deletes a user by id from the database
     * @param id
     */
    @Transactional
    void deleteById(int id);

    /**
     * this methods finds and lists all users in the database
     * @return
     */
    List<User> findAll();

    /**
     * this method finds a user by their username
     * @param username
     * @return
     */
    Optional<User> findByUsername(String username);
    //Optional<User> findByUsernameAndPassword(String username, String password);

    /**
     * this method finds a user by their username and id
     * @param username
     * @param id
     * @return
     */
    User findByUsernameAndId(String username, int id);
}
