package dk.ek.kinoxp.domain;

import java.util.Objects;

public final class Seat {
    private final String row; //SKAL VÆRE UPPERCASE BOGSTAV!!
    private final int number; //Bare et tal fra 1 og op

    public Seat(final String row, final int number) {
        if (row == null || row.isBlank()) throw new IllegalArgumentException("Row required");
        this.row = row;
        this.number = number;
    }

    public String row() {return row;}
    public int number() {return number;}

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Seat s)) return false;
        return number == s.number && row.equals(s.row);
    }

    @Override public int hashCode() {return Objects.hash(row, number);}
    @Override public String toString() {return row + number;}
}
