package dk.ek.kinoxp.web.dto;

import dk.ek.kinoxp.domain.enums.TicketType;
import jakarta.validation.constraints.*;

public class SeatPurchaseForm {
    @NotNull private Long screeningId;
    @NotBlank private String rowLetter;
    @NotBlank @Min(1) private Integer seatNumber;
    @NotNull private TicketType type;

    public Long getScreeningId() { return screeningId;}
    public String getRowLetter() { return rowLetter;}
    public Integer getSeatNumber() { return seatNumber;}
    public TicketType getType() { return type;}

    public void setScreeningId(Long screeningId) { this.screeningId = screeningId;}
    public void setRowLetter(String rowLetter) { this.rowLetter = rowLetter;}
    public void setSeatNumber(Integer seatNumber) { this.seatNumber = seatNumber;}
    public void setType(TicketType type) { this.type = type;}
}
