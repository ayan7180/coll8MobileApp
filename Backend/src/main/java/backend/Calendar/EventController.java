package backend.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
//@RequestMapping("/api")
public class EventController {

    @Autowired
    EventRepository eventRepository;


    //README: verify user is superuser before allowing altering for classwide calendars

    /**
     * creates an event
     * @param event
     * @return msg
     * frontend should send a string array event
     * ["classname", "eventname", "event description"]
     */
    @PostMapping(path="/event")
    public ResponseEntity<Event> createEvent(@RequestBody String[] event){
        Event event1 = new Event();
        String msg;
        event1.setClassName(event[0]);
        event1.setEvent(event[1]);

        if(event1.getEvent() == null || event1.getClassName() == null){
            return ResponseEntity.badRequest().body(null);

        }
//        if(event.length > 2){
//            msg = "string array too long check parameters";
//
//        }else{
//            event1.setClassName(event[0]);
//            event1.setEvent(event[1]);
//
//            //event1.setDescription(event[2]);
//            msg = "event created successfully";
           eventRepository.save(event1);
//        }
        return ResponseEntity.ok(event1);
    }

    //read event - returns an event object
    @RequestMapping(path="/event")
    public Event getEvent(@RequestBody Event event ){
        Optional<Event> e = eventRepository.findById(event.getId());
        return e.orElse(null);
    }



    //update-edit eventname
    @PutMapping(path = "/event/{id}")
    public String updateEvent(@PathVariable int id, @RequestBody String eventName){
      Optional<Event> exist = eventRepository.findById(id);
      String msg;
      if(exist.isPresent()){
          exist.get().setEvent(eventName);
          eventRepository.save(exist);
          msg = "event name successfully updated";
      }else{
          msg = "event not found";
      }
      return msg;
    }

    //delete event by id
   @DeleteMapping(path = "/event")
    public String deleteEvent(@RequestParam int id){
        Optional<Event> existingEvent = eventRepository.findById(id);
        String msg;
        if(existingEvent.isPresent()){
            eventRepository.deleteById(existingEvent.get().getId());
            msg = "event deleted successfully";
        }else{
            msg = "event non existent";
        }
        return msg;
   }
}
