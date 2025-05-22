package backend.Phones;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import backend.Users.User;

@Entity
public class Phone {

    @Id
    @GeneratedValue
    private int id;
    private String model;
    private int year;
    private int numScratches;
    private boolean hasStickers;

    @OneToOne
    @JsonIgnore
    private User user;

    public Phone(String model, int year, int numScratches, boolean hasStickers){
        this.model = model;
        this.year = year;
        this.numScratches = numScratches;
        this.hasStickers = hasStickers;
    }

    public Phone() {

    }

}
