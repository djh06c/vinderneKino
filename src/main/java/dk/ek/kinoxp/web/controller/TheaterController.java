package dk.ek.kinoxp.web.controller;

import dk.ek.kinoxp.domain.Theater;
import dk.ek.kinoxp.domain.SeatGrid;
import dk.ek.kinoxp.web.dto.TheaterDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
        import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/theaters")
public class TheaterController {

    /** HTML: Liste over jeres sale (statisk 1 og 2) */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("theaters", List.of(
                new TheaterDto(1, Theater.SAL_1.getRows(), Theater.SAL_1.getSeatsPerRow(), Theater.SAL_1.getCapacity()),
                new TheaterDto(2, Theater.SAL_2.getRows(), Theater.SAL_2.getSeatsPerRow(), Theater.SAL_2.getCapacity())
        ));
        return "theaters/list";
    }

    /** HTML: Vis én sal */
    @GetMapping("/{auditorium}")
    public String show(@PathVariable Integer auditorium, Model model) {
        var spec = Theater.of(auditorium);
        if (spec == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ukendt sal");
        model.addAttribute("auditorium", auditorium);
        model.addAttribute("rows", spec.getRows());
        model.addAttribute("seatsPerRow", spec.getSeatsPerRow());
        model.addAttribute("capacity", spec.getCapacity());
        model.addAttribute("grid", SeatGrid.all(spec)); // fx ["A1","A2",...]
        return "theaters/show";
    }

    // -------- API (JSON) til din form/JS --------

    @GetMapping(value="/{auditorium}/spec", produces="application/json")
    @ResponseBody
    public TheaterDto spec(@PathVariable Integer auditorium) {
        var t = Theater.of(auditorium);
        if (t == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ukendt sal");
        return new TheaterDto(auditorium, t.getRows(), t.getSeatsPerRow(), t.getCapacity());
    }

    @GetMapping(value="/{auditorium}/grid", produces="application/json")
    @ResponseBody
    public List<String> grid(@PathVariable Integer auditorium) {
        var t = Theater.of(auditorium);
        if (t == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ukendt sal");
        return SeatGrid.all(t);
    }
}
