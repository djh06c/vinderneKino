package dk.ek.kinoxp.web.controller;

import dk.ek.kinoxp.domain.Movie;
import dk.ek.kinoxp.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/movies")
public class MovieController {

    private final MovieService service;
    public MovieController(MovieService service) { this.service = service; }

    // LIST (GET /movies)
    @GetMapping
    public String list(Model model,
                       @RequestParam(value = "msg", required = false) String msg) {
        var all = service.getAllMovies();
        var today = LocalDate.now();

        var active = all.stream()
                .filter(m -> (m.getPremiereDate() != null && !m.getPremiereDate().isAfter(today))
                        && (m.getEndDate() != null && !m.getEndDate().isBefore(today)))
                .toList();

        Set<Long> activeIds = active.stream().map(Movie::getId).collect(Collectors.toSet());
        var others = all.stream()
                .filter(m -> m.getId() == null || !activeIds.contains(m.getId()))
                .toList();

        model.addAttribute("today", today);
        model.addAttribute("movies", all);          // bagud-kompatibel
        model.addAttribute("activeMovies", active); // sektion 1
        model.addAttribute("otherMovies", others);  // sektion 2
        if (msg != null) model.addAttribute("msg", msg);
        return "movies/list";
    }

    // CREATE FORM (GET /movies/ny)
    @GetMapping("/ny")
    public String createForm(Model model) {
        if (!model.containsAttribute("movie")) {
            model.addAttribute("movie", new Movie());
        }
        return "movies/form";
    }

    // CREATE SUBMIT (POST /movies)
    @PostMapping
    public String create(@Valid @ModelAttribute("movie") Movie movie,
                         BindingResult br,
                         RedirectAttributes ra) {

        if (movie.getPremiereDate() != null && movie.getEndDate() != null
                && movie.getEndDate().isBefore(movie.getPremiereDate())) {
            br.rejectValue("endDate", "date.order", "Slutdato skal være efter premiere.");
        }

        if (br.hasErrors()) {
            // behold valideringsfejl og input ved redirect tilbage til /ny
            ra.addFlashAttribute("org.springframework.validation.BindingResult.movie", br);
            ra.addFlashAttribute("movie", movie);
            return "redirect:/movies/ny";
        }

        service.save(movie);
        ra.addFlashAttribute("msg", "Filmen blev oprettet.");
        return "redirect:/movies";
    }

    // EDIT FORM (GET /movies/{id}/rediger)
    @GetMapping("/{id}/rediger")
    public String editForm(@PathVariable Long id, Model model) {
        var movie = service.get(id);
        if (movie == null) throw new NoSuchElementException("Film med id " + id + " findes ikke.");
        if (!model.containsAttribute("movie")) {
            model.addAttribute("movie", movie);
        }
        return "movies/form";
    }

    // UPDATE SUBMIT (POST /movies/{id})
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("movie") Movie movie,
                         BindingResult br,
                         RedirectAttributes ra) {

        if (movie.getPremiereDate() != null && movie.getEndDate() != null
                && movie.getEndDate().isBefore(movie.getPremiereDate())) {
            br.rejectValue("endDate", "date.order", "Slutdato skal være efter premiere.");
        }

        if (br.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.movie", br);
            ra.addFlashAttribute("movie", movie);
            return "redirect:/movies/{id}/rediger";
        }

        movie.setId(id);
        service.save(movie);
        ra.addFlashAttribute("msg", "Filmen blev opdateret.");
        return "redirect:/movies";
    }

    // DELETE (POST /movies/{id}/slet)
    @PostMapping("/{id}/slet")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        service.delete(id);
        ra.addFlashAttribute("msg", "Filmen blev slettet.");
        return "redirect:/movies";
    }

    // (Valgfri) VIEW (GET /movies/{id})
    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        var movie = service.get(id);
        if (movie == null) throw new NoSuchElementException("Film med id " + id + " findes ikke.");
        model.addAttribute("movie", movie);
        return "movies/view";
    }
}
