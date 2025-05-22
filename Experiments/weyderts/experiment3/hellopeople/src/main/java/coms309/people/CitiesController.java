package coms309.people;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * Controller used to showcase Create and Read from a LIST
 *
 * @author Vivek Bengre
 */

@RestController
public class CitiesController {

    HashMap<String, City> cityList = new HashMap<>();

    //CRUDL (create/read/update/delete/list)
    // use POST, GET, PUT, DELETE, GET methods for CRUDL

    // List Operation
    @GetMapping("/cities")
    public HashMap<String, City> getAllCities() {
        return cityList;
    }

    // Create Operation
    @PostMapping("/cities")
    public String createCity(@RequestBody City city) {
        System.out.println(city);
        cityList.put(city.get_name(), city);
        return "New city "+ city.get_name() + " Saved";
    }

    // Read Operation
    @GetMapping("/cities/{name}")
    public City getCity(@PathVariable String name) {
        return cityList.get(name);
    }

    // Update Operation
    @PutMapping("/cities/{name}")
    public City updateCity(@PathVariable String name, @RequestBody City city) {
        cityList.replace(name, city);
        return cityList.get(name);
    }

    // Delete Operation
    @DeleteMapping("/cities/{name}")
    public HashMap<String, City> deleteCity(@PathVariable String name) {
        cityList.remove(name);
        return cityList;
    }

    // List Operation (List all people from a city)
    @GetMapping("/cities/{name}/residents")
    public List<Person> getPeopleFromCity(@PathVariable String name) {
        return PeopleController.searchPeopleByCity(name);
    }

}

