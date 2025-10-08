package dk.ek.kinoxp.repository;

import dk.ek.kinoxp.domain.Screening;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    // Alle visninger af en film på en bestemt dag, i stigende rækkefølge efter tidspunkt på dagen.
    List<Screening> findByMovieIdAndDateOrderByTimeAsc(Long movieId, LocalDate date);

    // Alle visninger i et datointerval - sorteret efter Time ascend.
    List<Screening> findByMovieIdAndDateBetweenOrderByDateAscTimeAsc(Long movieId, LocalDate from, LocalDate to);

    // Alle visninger op en given dag, ikke sorteret efter filmId
    List<Screening> findByDateOrderByTimeAsc(LocalDate date);

}
