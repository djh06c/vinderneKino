package dk.ek.kinoxp.web.controller;

import dk.ek.kinoxp.domain.Movie;
import dk.ek.kinoxp.domain.Screening;
import dk.ek.kinoxp.service.MovieService;
import dk.ek.kinoxp.service.ScreeningService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/screenings")
public class ScreeningController {
    private final ScreeningService screeningService;
    private final MovieService movieService;

    public ScreeningController(ScreeningService screeningService, MovieService movieService) {
        this.screeningService = screeningService;
        this.movieService = movieService;
    }


    //Liste af film på bestemt dato
    @GetMapping
    public String listForMovieOnDate(
            @RequestParam(required = false) Long movieId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        LocalDate d = (date != null) ? date : LocalDate.now();
        model.addAttribute("date", d);

        List<Movie> movies = movieService.getAllMovies();
        model.addAttribute("movies", movies);

        if (movieId != null) {
            model.addAttribute("selectedMovie", movieService.get(movieId));
            List<Screening> screenings = screeningService.forMovieOnDate(movieId, d);
            model.addAttribute("screenings", screenings);
        }
        return "screenings/list";
    }

    //Create screening
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("screening", new Screening());
        model.addAttribute("movies", movieService.getAllMovies());
        return "screenings/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("screening") Screening screening, BindingResult br, Model model) {
        if (screening.getAvailableSeats() <= 0) {
            br.rejectValue("availableSeats", "seats.positive", "Antal pladser skal være > 0.");
        }
        if (br.hasErrors()){
            model.addAttribute("movies", movieService.getAllMovies());
            return "screenings/form";
        }
        screeningService.save(screening);
        return "redirect:/screenings/";
    }

    //Rediger i forestilling
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("screening", screeningService.getById(id));
        model.addAttribute("movies", movieService.getAllMovies());
        return "screenings/form";
    }

    //Opdater forestilling
    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("screening") Screening screening,
                         BindingResult br,Model model) {
        if (screening.getAvailableSeats() <= 0) {
            br.rejectValue("availableSeats", "seats.positive", "Antal pladser skal være > 0.");
        }
        if (br.hasErrors()){
            model.addAttribute("movies", movieService.getAllMovies());
            return "screenings/form";
        }
        screening.setId(id);
        screeningService.save(screening);
        return "redirect:/screenings";
    }

    //Slet Forestilling
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        screeningService.deleteById(id);
        return "redirect:/screenings";
    }

}
