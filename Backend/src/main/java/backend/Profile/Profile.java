package backend.Profile;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import backend.Users.User;
//IDEA: maybe import users from our database and just use what we have and add a few extra fields but mainly reference Users

/**
 * @author Soma Germano
 * this class handles the construction of an instance of a profile object
 */
@Entity
@Table(name="profile")
public class Profile {
    //private fields
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private String email;
    private String profileImage;

    @OneToOne
    @JsonIgnore
    private User user;

    //constructor(s)

    /**
     * constructor to create a new instance of a profile object
     * @param user
     */
    public Profile(User user){
        this.name = user.getUsername();
        this.email = user.getEmailId();
        this.profileImage = user.getProfileImage();
        this.id = user.getId();
    }

    /**
     * default constructor
     */
    public Profile(){}
    //getters + setters -

    /**
     * retrieves a profile's id
     * @return
     */
    public int getProfileId() { return id; }

    /**
     * updates a profile's id
     * @param profileId
     */
    public void setProfileId(int profileId) {this.id = profileId;  }

    /**
     * retrieves a profile's email
     * @return
     */
    public String getEmail(){ return email; }

    /**
     * updates a user's email
     * @param user
     */
    public void setEmail(User user){ this.email = user.getEmailId();}

    /**
     * retrieves a profile's user
     * @return
     */
    public User getUser(){ return user; }

    /**
     * updates a profile's user
     * @param user
     */
    public void setUser(User user){ this.user = user; }

    /**
     * updates a profile's image file name
     * @param profileImage
     */
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
