// dk.ek.kinoxp.domain.Ticket
package dk.ek.kinoxp.domain;

import dk.ek.kinoxp.domain.enums.TicketType;
import jakarta.persistence.*;

@Entity
@Table(name = "tickets",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_ticket_screening_row_seat",
                columnNames = {"screening_id", "row_letter", "seat_number"}
        ))
public class Ticket {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "screening_id")
    private Screening screening;

    @Column(name="row_letter", nullable=false, length=3)
    private String rowLetter;

    @Column(name="seat_number", nullable=false)
    private int seatNumber;

    @Column(name="ticket_type", nullable=false)
    @Enumerated(EnumType.STRING)
    private TicketType ticketType;

    // (valgfrit) kundeinfo, pris, tidspunkt osv.

    // getters/setters
    public Long getId() { return id; }
    public Screening getScreening() { return screening; }
    public void setScreening(Screening s) { this.screening = s; }
    public String getRowLetter() { return rowLetter; }
    public void setRowLetter(String r) { this.rowLetter = r.toUpperCase(); }
    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int n) { this.seatNumber = n; }
    public TicketType getTicketType() { return ticketType; }
    public void setTicketType(TicketType t) { this.ticketType = t; }
}
