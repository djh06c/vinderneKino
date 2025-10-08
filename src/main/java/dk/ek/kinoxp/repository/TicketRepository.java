package dk.ek.kinoxp.repository;

import dk.ek.kinoxp.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByScreeningId(Long screeningId);

    long countByScreeningId(Long screeningId);

    boolean existsByScreening_IdAndRowLetterIgnoreCaseAndSeatNumber(Long screeningId, String rowLetter, int seatNumber);

    @Query("select t.rowLetter, t.seatNumber from Ticket t where t.screening.id = :screeningId")
    List<Object[]> findOccupiedPairs(@Param("screeningId") Long screeningId);
}
