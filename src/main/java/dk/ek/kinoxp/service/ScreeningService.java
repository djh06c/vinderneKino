package dk.ek.kinoxp.service;

import dk.ek.kinoxp.domain.Screening;
import dk.ek.kinoxp.repository.ScreeningRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScreeningService {
    private final ScreeningRepository repo;

    public ScreeningService(ScreeningRepository repo) {
        this.repo = repo;
    }

    public List<Screening> forMovieOnDate(Long movieId, LocalDate date) {
        return repo.findByMovieIdAndDateOrderByTimeAsc(movieId, date);
    }

    public List<Screening> getAll() {return repo.findAll();}
    public Screening get(Long id) {return repo.findById(id).orElseThrow();}
    public Screening save(Screening screening) {return repo.save(screening);}
    public void delete(Long id) {repo.deleteById(id);}
}