package dk.ek.kinoxp.web.controller;

import dk.ek.kinoxp.domain.Movie;
import dk.ek.kinoxp.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/movies")
public class MovieController {
    private final MovieService service;
    public MovieController(MovieService service) {this.service = service;}

    @GetMapping
    public String MovieList(Model model) {
        model.addAttribute("movies", service.getAllMovies());
        return "movies/list";
    }

    @GetMapping("/ny")
    public String createForm(Model model) {
        model.addAttribute("movie", new Movie());
        return "movies/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("movie") Movie movie, BindingResult br) {
        if (movie.getPremiereDate() != null && movie.getEndDate() != null
                && movie.getEndDate().isBefore(movie.getPremiereDate())) {
            br.rejectValue("endDate", "date.order", "Slutdato skal være efter premiere.");
        }
        if (br.hasErrors()) return "movies/form";
        service.save(movie);
        return "redirect:/movies";
    }

    @GetMapping("/{id}/rediger")
    public String editForm(@PathVariable Long id, Model model){
        model.addAttribute("movie", service.get(id));
        return "movies/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("movie") Movie movie, BindingResult br) {
        if (movie.getPremiereDate() != null && movie.getEndDate() != null
                && movie.getEndDate().isBefore(movie.getPremiereDate())) {
            br.rejectValue("endDate", "date.order", "Slutdato skal være efter premiere.");
        }
        if (br.hasErrors()) return "movies/form";
        movie.setId(id);
        service.save(movie);
        return "redirect:/movies";
    }

    @PostMapping("/{id}/slet")
    public String delete(@PathVariable Long id){
        service.delete(id);
        return "redirect:/movies";
    }
}
