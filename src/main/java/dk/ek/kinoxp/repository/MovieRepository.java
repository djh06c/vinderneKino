package dk.ek.kinoxp.repository;

import dk.ek.kinoxp.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByPremiereDateLessThanEqualAndEndDateGreaterThanEqual(java.time.LocalDate from, java.time.LocalDate to);
}
