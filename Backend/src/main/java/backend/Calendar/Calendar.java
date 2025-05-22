package backend.Calendar;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Calendar {
    //private fields

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToMany
    private List<Event> events; //list all events

    //constructor
    public Calendar(){

    }
    //getters and setters
    public int getId(){ return id; }

    public void setId(int id) {
        this.id = id;
    }

    public List<Event> getEvents() {
        return events;
    }
    public void setEvents(List<Event> events) {
        this.events = events;
    }
    public void addEvents(Event event){
        this.events.add(event);
    }
}
