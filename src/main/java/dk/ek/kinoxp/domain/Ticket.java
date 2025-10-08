package dk.ek.kinoxp.domain;

import dk.ek.kinoxp.domain.enums.TicketType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    private Screening screening;

    @Enumerated(EnumType.STRING)
    private TicketType type;

    @Positive
    private int price;

    private LocalDateTime soldAt = LocalDateTime.now();


    public Long getId() { return id; }
    public Screening getScreening() { return screening; }
    public TicketType getType() { return type; }
    public int getPrice() { return price; }
    public LocalDateTime getSoldAt() { return soldAt; }

    public void setId(Long id) { this.id = id; }
    public void setScreening(Screening screening) { this.screening = screening; }
    public void setType(TicketType type) { this.type = type; }
    public void setPrice(int price) { this.price = price; }
    public void setSoldAt(LocalDateTime soldAt) { this.soldAt = soldAt; }
}
