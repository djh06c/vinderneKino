package dk.ek.kinoxp.web.dto;

public record SeatRef(String row, int number) {
    public SeatRef {
        if (row == null || row.isBlank()) throw new IllegalArgumentException("row required");
        row = row.toUpperCase();
        if (number < 1) throw new IllegalArgumentException("number must be >= 1");
    }
}
