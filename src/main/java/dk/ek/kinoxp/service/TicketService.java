package dk.ek.kinoxp.service;

import dk.ek.kinoxp.domain.Screening;
import dk.ek.kinoxp.domain.Ticket;
import dk.ek.kinoxp.repository.ScreeningRepository;
import dk.ek.kinoxp.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketService {
    private final TicketRepository ticketRepo;
    private final ScreeningRepository screeningRepo;

    public TicketService(TicketRepository ticketRepo, ScreeningRepository screeningRepo) {
        this.ticketRepo = ticketRepo;
        this.screeningRepo = screeningRepo;
    }

    public List<Ticket> getAll() {
        return ticketRepo.findAll();
    }

    public List<Ticket> getByScreening(Long screeningId) {
        return ticketRepo.findByScreeningId(screeningId);
    }

    @Transactional
    public Ticket sellTicket(Ticket ticket) {
        Screening s = ticket.getScreening();
        if (s.getAvailableSeats() <= 0) {
            throw new IllegalStateException("Ingen ledige pladser!");
        }
        s.setAvailableSeats(s.getAvailableSeats() - 1);
        screeningRepo.save(s);
        return ticketRepo.save(ticket);
    }
}
