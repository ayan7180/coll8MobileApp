package backend.Calendar;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * this class handles the event object
 * calendar and event relationship should be onetomany
 */
@Entity
public class Event {
    //private fields
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String event;
    private LocalDate eventDate;
    //private String description;
    private String className;

    //constructor
    public Event(String className, String event){

        this.className = className;
        this.event = event;
        //this.description = description;
    }
    //default constructor
    public Event(){}

    //getters and setters
    public int getId() { return id; }
    public String getEvent(){ return event; }
    public void setEvent(String event){ this.event = event;}
    public LocalDate getEventDate(){ return eventDate; }
    public void setEventDate(LocalDate eventDate){ this.eventDate = eventDate; }
//    public String getDescription(){ return description; }
//    public void setDescription(String newDescription){ this.description = newDescription; }
    public String getClassName(){ return className; }
    public void setClassName(String newClassName){ this.className = newClassName; }


}
