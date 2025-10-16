package dk.ek.kinoxp.web.dto;

import java.util.List;

public record HoldSeatRequest(List<SeatRef> seats) {
}
