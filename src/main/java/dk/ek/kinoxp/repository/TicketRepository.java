package dk.ek.kinoxp.repository;

import dk.ek.kinoxp.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByScreeningId(Long screeningId);
}
