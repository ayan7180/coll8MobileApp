package org.springframework.samples.petclinic.system;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        return "Welcome</br> Go to localhost:8080/owners/create to create dummy data </br>";
    }
    //im getting an error typing localhost:8080/owners/create bc create cannot be converted to int
    //wrong link given should be owner not owners
}
