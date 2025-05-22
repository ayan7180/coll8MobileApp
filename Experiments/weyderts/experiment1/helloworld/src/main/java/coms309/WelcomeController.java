package coms309;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
class WelcomeController {

    @RequestMapping("/")
    public String screen_root() {
        return "I want to boom boom";
    }

    @RequestMapping("/smallBoomBoom/{name1}")
    public String screen_BoomBoom1(@PathVariable String name1) {
        return ("" + name1 + " did a small Boom Boom!");
    }

    @RequestMapping("/smallBoomBoom/{name1}/bigBoomBoom/{name2}")
    public String screen_BoomBoom2(@PathVariable String name1, @PathVariable String name2) {
        return ("" + name1 + " did a small Boom Boom, and " + name2 + " did a big Boom Boom!");
    }
}
