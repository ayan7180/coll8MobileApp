package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        return "Hola and Welcome to COMS 309";
    }

    @GetMapping("{name}")
    public String welcome(@PathVariable String name) {
       name = "soma";
       String group = "4_rasel_3";
        return "Hola and Welcome to COMS 309: " + name.concat("\n Group assignment: " + group);
    }
}
