package backend.FileLibrary;

//imports
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


//

/**
 * @author Soma Germano
 * this class handles the CRUD operations provided/inherited by JPA repository
 */
public interface FileLibraryRepository extends JpaRepository<FileLibrary, String> {
    //

    /**
     * this inherited method searches a file by its name
     * @param fileName
     * @return
     */
    @Query("SELECT f from FileLibrary f WHERE f.fileName = ?1")
    Optional<FileLibrary> findByName(String fileName);

    /**
     * this inherited method searches a file by its id
     * @param id
     * @return
     */
    @Query("SELECT f from FileLibrary f WHERE f.id = ?1")
    Optional<FileLibrary> findById(int id);

    /**
     * this inherited method finds all files in the filelibrary
     *
     */
    List<FileLibrary> findAll();

    /**
     * this inherited method saves files to the database
     * @param file
     * @return
     */
    FileLibrary save(FileLibrary file);

    /**
     * this inherited method deletes file by id
     * @param id
     */
    // @Query("SELECT f from FileLibrary f WHERE f.fileName = ?1")
    @Transactional
    void deleteById(int id);

}
