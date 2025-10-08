package dk.ek.kinoxp.web.dto;

public class TheaterDto {
    public int auditorium;
    public int rows;
    public int seatsPerRow;
    public int capacity;

    public TheaterDto() {}
    public TheaterDto(int auditorium, int rows, int seatsPerRow, int capacity) {
        this.auditorium = auditorium; this.rows = rows; this.seatsPerRow = seatsPerRow; this.capacity = capacity;
    }
}
