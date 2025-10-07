package dk.ek.kinoxp.service;

import dk.ek.kinoxp.domain.RowLetters;
import dk.ek.kinoxp.domain.Screening;
import dk.ek.kinoxp.domain.Theater;
import dk.ek.kinoxp.domain.Ticket;
import dk.ek.kinoxp.domain.enums.TicketType;
import dk.ek.kinoxp.repository.ScreeningRepository;
import dk.ek.kinoxp.repository.TicketRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {
    private final TicketRepository ticketRepo;
    private final ScreeningRepository screeningRepo;

    public TicketService(TicketRepository ticketRepo, ScreeningRepository screeningRepo) {
        this.ticketRepo = ticketRepo;
        this.screeningRepo = screeningRepo;
    }

    // --- Queries / helpers ---

    public List<Ticket> getAll() {
        return ticketRepo.findAll();
    }

    public Ticket get(Long id) {
        return ticketRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Ticket ikke fundet"));
    }

    public List<Ticket> getByScreening(Long screeningId) {
        return ticketRepo.findByScreeningId(screeningId);
    }

    public long countSold(Long screeningId) {
        return ticketRepo.countByScreeningId(screeningId);
    }

    public List<Object[]> occupiedPairs(Long screeningId) {
        return ticketRepo.findOccupiedPairs(screeningId);
    }

    /** Kapacitet for en screenings sal (20×12 eller 22×16) */
    public int capacityFor(Screening s) {
        var spec = Theater.of(s.getAuditorium());
        if (spec == null) throw new IllegalStateException("Ukendt sal: " + s.getAuditorium());
        return spec.getCapacity();
    }

    /** Ledige pladser = kapacitet - solgte (afledt; ingen felt-afhængighed) */
    public int availableSeats(Long screeningId) {
        Screening s = screeningRepo.findById(screeningId)
                .orElseThrow(() -> new IllegalArgumentException("Screening ikke fundet"));
        return capacityFor(s) - (int) countSold(screeningId);
    }

    // --- Commands ---

    /** Sælg ET sæde (uden type) – bevares for bagudkompatibilitet. */
    @Transactional
    public Ticket sellSeat(Long screeningId, String rowLetter, int seatNumber) {
        return sellSeat(screeningId, rowLetter, seatNumber, null);
    }

    /** Sælg ET sæde (med type). Kast IllegalStateException ved dobbeltsalg. */
    @Transactional
    public Ticket sellSeat(Long screeningId, String rowLetter, int seatNumber, TicketType type) {
        Screening s = screeningRepo.findById(screeningId)
                .orElseThrow(() -> new IllegalArgumentException("Screening ikke fundet"));

        var spec = Theater.of(s.getAuditorium());
        if (spec == null) throw new IllegalStateException("Ukendt sal: " + s.getAuditorium());

        String row = normalizeRow(rowLetter);
        validateSeatWithinSpec(spec.getRows(), spec.getSeatsPerRow(), row, seatNumber);

        // Hurtig pre-check (race-safe, da vi også fanger unik-constraint nedenfor)
        if (ticketRepo.existsByScreening_IdAndRowLetterIgnoreCaseAndSeatNumber(screeningId, row, seatNumber)) {
            throw new IllegalStateException("Sæde " + row + seatNumber + " er optaget.");
        }

        try {
            Ticket t = new Ticket();
            t.setScreening(s);
            t.setRowLetter(row); // gemmes uppercase
            t.setSeatNumber(seatNumber);
            if (type != null) t.setType(type);
            return ticketRepo.save(t);
        } catch (DataIntegrityViolationException e) {
            // En anden transaktion snuppede sædet imens
            throw new IllegalStateException("Sæde " + row + seatNumber + " er netop blevet optaget.", e);
        }
    }

    /** Sælg FLERE sæder i én transaktion (alt-eller-intet). */
    @Transactional
    public List<Ticket> sellSeats(Long screeningId, List<String> seatCodes, TicketType type) {
        Screening s = screeningRepo.findById(screeningId)
                .orElseThrow(() -> new IllegalArgumentException("Screening ikke fundet"));

        var spec = Theater.of(s.getAuditorium());
        if (spec == null) throw new IllegalStateException("Ukendt sal: " + s.getAuditorium());

        // 1) Valider alle først (så vi fejler tidligt og ruller alt tilbage)
        List<RowSeat> parsed = new ArrayList<>(seatCodes.size());
        for (String code : seatCodes) {
            RowSeat rs = parseSeatCode(code); // fx "A12" eller "A-12"
            validateSeatWithinSpec(spec.getRows(), spec.getSeatsPerRow(), rs.row, rs.seat);
            parsed.add(rs);
        }

        // 2) Gem alle
        List<Ticket> out = new ArrayList<>(parsed.size());
        try {
            for (RowSeat rs : parsed) {
                Ticket t = new Ticket();
                t.setScreening(s);
                t.setRowLetter(rs.row);
                t.setSeatNumber(rs.seat);
                if (type != null) t.setType(type);
                out.add(ticketRepo.save(t));
            }
            return out;
        } catch (DataIntegrityViolationException e) {
            // Hvis ét sæde er taget, ruller hele transaktionen tilbage
            throw new IllegalStateException("Et eller flere valgte sæder blev netop optaget. Prøv igen.", e);
        }
    }

    // --- Legacy adapter (bevar din signatur) ---
    @Deprecated
    @Transactional
    public Ticket sellTicket(Ticket ticket) {
        // hvis Ticket.type er påkrævet, så brug overloaden med type
        TicketType type = null;
        try {
            type = ticket.getType();
        } catch (Exception ignored) {}
        return sellSeat(ticket.getScreening().getId(), ticket.getRowLetter(), ticket.getSeatNumber(), type);
    }

    // --- private helpers ---

    private static String normalizeRow(String rowLetter) {
        if (rowLetter == null) throw new IllegalArgumentException("Række skal udfyldes");
        return rowLetter.trim().toUpperCase();
    }

    private static void validateSeatWithinSpec(int maxRows, int maxPerRow, String rowLetter, int seatNumber) {
        int rowIndex = RowLetters.toIndex(rowLetter); // A->1, AA->27
        if (rowIndex < 1 || rowIndex > maxRows) {
            throw new IllegalArgumentException("Ugyldig række: " + rowLetter);
        }
        if (seatNumber < 1 || seatNumber > maxPerRow) {
            throw new IllegalArgumentException("Ugyldigt sædenr for række " + rowLetter + ": " + seatNumber);
        }
    }

    /** Tillader "A12" eller "A-12" og returnerer normaliseret (row, seat). */
    private static RowSeat parseSeatCode(String code) {
        if (code == null) throw new IllegalArgumentException("Ugyldigt sædeformat");
        String s = code.trim().toUpperCase().replaceAll("\\s+", "");
        if (!s.matches("^[A-Z]+-?\\d+$")) throw new IllegalArgumentException("Ugyldigt sædeformat: " + code);
        String row, num;
        int dash = s.lastIndexOf('-');
        if (dash >= 0) {
            row = s.substring(0, dash);
            num = s.substring(dash + 1);
        } else {
            int i = 0; while (i < s.length() && Character.isLetter(s.charAt(i))) i++;
            row = s.substring(0, i);
            num = s.substring(i);
        }
        return new RowSeat(row, Integer.parseInt(num));
    }

    private record RowSeat(String row, int seat) {}
}
