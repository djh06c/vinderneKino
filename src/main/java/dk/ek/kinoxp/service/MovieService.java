package dk.ek.kinoxp.service;

import org.springframework.stereotype.Service;
import dk.ek.kinoxp.domain.Movie;
import dk.ek.kinoxp.repository.MovieRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class MovieService {
    private final MovieRepository movieRepo;
    public MovieService(MovieRepository movieRepo) { this.movieRepo = movieRepo; }

    public List<Movie> getAllMovies() { return movieRepo.findAll(); }
    public Movie get(Long id) {return movieRepo.findById(id).orElseThrow();}
    public Movie save(Movie movie) {return movieRepo.save(movie);}
    public void delete(Long id) { movieRepo.deleteById(id);}

    public List<Movie> current() {
        LocalDate today = LocalDate.now();
        return movieRepo.findByPremiereDateLessThanEqualAndEndDateGreaterThanEqual(today, today);
    }
}
