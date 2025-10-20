package dk.ek.kinoxp.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home/home";
    }

    @GetMapping("/billetter")
    public String tickets() {
        return "screenings/list";
    }

    @GetMapping("/mad-drikke")
    public String food() {
        return "items/list";
    }

    @GetMapping("/kalender")
    public String calendar() {
        return "home/placeholder";
    }

    @GetMapping("/vagtplan")
    public String shifts() {
        return "shifts/list";
    }
}
