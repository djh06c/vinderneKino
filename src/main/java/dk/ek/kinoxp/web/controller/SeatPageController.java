package dk.ek.kinoxp.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/screenings")
public class SeatPageController {
    @GetMapping("/{id}/seats")
    public String page(@PathVariable("id") long screeningId, Model model) {
        model.addAttribute("screeningId", screeningId);
        return "screenings/seats";
    }
}
