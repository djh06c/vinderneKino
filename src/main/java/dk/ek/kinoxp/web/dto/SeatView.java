package dk.ek.kinoxp.web.dto;

import dk.ek.kinoxp.domain.enums.SeatState;

public record SeatView(
        String row,
        int number,
        boolean isAisle,
        SeatState status
){}