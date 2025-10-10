package dk.ek.kinoxp.web.controller;

import dk.ek.kinoxp.domain.Movie;
import dk.ek.kinoxp.domain.Screening;
import dk.ek.kinoxp.domain.Theater;
import dk.ek.kinoxp.service.MovieService;
import dk.ek.kinoxp.service.ScreeningService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    /** Gør film-listen altid tilgængelig i views (inkl. ved valideringsfejl). */
    @ModelAttribute("movies")
    public List<Movie> movies() {
        return movieService.getAllMovies();
    }

    // ==============================
    // LISTE over forestillinger
    // ==============================
    @GetMapping
    public String listForMovieOnDate(
            @RequestParam(required = false) Long movieId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        LocalDate d = (date != null) ? date : LocalDate.now();
        model.addAttribute("date", d);

        // ← Eksplicit: sørg for at dropdown altid har data
        model.addAttribute("movies", movieService.getAllMovies());

        if (movieId != null) {
            Movie selected = movieService.get(movieId);
            model.addAttribute("selectedMovie", selected);
            model.addAttribute("screenings", screeningService.forMovieOnDate(movieId, d));
        }
        return "screenings/list";
    }


    // ==============================
    // OPRET ny forestilling (GET)
    // ==============================
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("screening", new Screening());
        return "screenings/form";
    }

    // ==============================
    // OPRET ny forestilling (POST)
    // ==============================
    @PostMapping
    public String create(@Valid @ModelAttribute("screening") Screening screening,
                         BindingResult br,
                         Model model,
                         RedirectAttributes ra) {

        // Sæt/plafondér ledige pladser ift. salens kapacitet
        autoFillAndClampSeatsToCapacity(screening);

        // Basal validering (inkl. seats <= capacity)
        validateScreeningBasics(screening, br);

        if (br.hasErrors()) {
            return "screenings/form";
        }

        // Knyt movie-entity (ID er tjekket i validateScreeningBasics)
        Long movieId = screening.getMovie().getId();
        Movie movie = movieService.get(movieId);
        screening.setMovie(movie);

        // Dato skal ligge i filmens periode
        if (screening.getDate() != null &&
                (screening.getDate().isBefore(movie.getPremiereDate())
                        || screening.getDate().isAfter(movie.getEndDate()))) {
            br.rejectValue("date", "date.range", "Datoen ligger uden for filmens premiereperiode.");
            return "screenings/form";
        }

        screeningService.save(screening);
        ra.addFlashAttribute("msg", "Forestillingen blev oprettet.");
        return "redirect:/screenings?movieId=" + movie.getId() + "&date=" + screening.getDate();
    }

    // ==============================
    // REDIGER forestilling (GET)
    // ==============================
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("screening", screeningService.get(id));
        return "screenings/form";
    }

    // ==============================
    // OPDATER forestilling (POST til /{id})
    // ==============================
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("screening") Screening screening,
                         BindingResult br,
                         Model model,
                         RedirectAttributes ra) {

        screening.setId(id);

        // Sæt/plafondér ledige pladser ift. salens kapacitet
        autoFillAndClampSeatsToCapacity(screening);

        // Basal validering (inkl. seats <= capacity)
        validateScreeningBasics(screening, br);

        if (br.hasErrors()) {
            return "screenings/form";
        }

        Long movieId = screening.getMovie().getId();
        Movie movie = movieService.get(movieId);
        screening.setMovie(movie);

        // Dato skal ligge i filmens periode
        if (screening.getDate() != null &&
                (screening.getDate().isBefore(movie.getPremiereDate())
                        || screening.getDate().isAfter(movie.getEndDate()))) {
            br.rejectValue("date", "date.range", "Datoen ligger uden for filmens premiereperiode.");
            return "screenings/form";
        }

        screeningService.save(screening);
        ra.addFlashAttribute("msg", "Forestillingen blev opdateret.");
        return "redirect:/screenings?movieId=" + movie.getId() + "&date=" + screening.getDate();
    }

    // ==============================
    // SLET forestilling
    // ==============================
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        screeningService.delete(id);
        ra.addFlashAttribute("msg", "Forestillingen blev slettet.");
        return "redirect:/screenings";
    }

    // ==============================
    // KØB-adapter (praktisk link fra liste/form)
    // ==============================
    @GetMapping("/{id}/buy")
    public String goBuy(@PathVariable Long id) {
        return "redirect:/screenings/" + id + "/seats";
    }


    // ------------------ Helpers ------------------

    /** Sæt tom/0 'availableSeats' til salens kapacitet og clamp til max. */
    private static void autoFillAndClampSeatsToCapacity(Screening s) {
        var spec = Theater.of(s.getAuditorium());
        if (spec == null) return;

        if (s.getAvailableSeats() <= 0) {
            s.setAvailableSeats(spec.getCapacity());
        }
        if (s.getAvailableSeats() > spec.getCapacity()) {
            s.setAvailableSeats(spec.getCapacity());
        }
    }

    /** Basal validering for sal, film og pladser (inkl. seats <= capacity). */
    private static void validateScreeningBasics(Screening screening, BindingResult br) {

        // Auditorium
        Integer aud = screening.getAuditorium();
        if (aud == null || (aud != 1 && aud != 2)) {
            br.rejectValue("auditorium", "auditorium.required", "Vælg sal 1 eller 2.");
        }

        // Movie
        Long movieId = (screening.getMovie() != null) ? screening.getMovie().getId() : null;
        if (movieId == null) {
            br.rejectValue("movie", "movie.required", "Vælg en film.");
            br.rejectValue("movie.id", "movie.required", "Vælg en film.");
        }

        // Seats > 0 og ikke over kapacitet (hvis sal er valid)
        if (screening.getAvailableSeats() <= 0) {
            br.rejectValue("availableSeats", "seats.positive", "Antal pladser skal være > 0.");
        }

        var spec = Theater.of(aud);
        if (spec != null && screening.getAvailableSeats() > spec.getCapacity()) {
            br.rejectValue("availableSeats", "seats.overCapacity",
                    "Maks i denne sal er " + spec.getCapacity() + " pladser.");
        }
    }
}
