package dk.ek.kinoxp.domain;

import java.util.ArrayList;
import java.util.List;

public class SeatGrid {
    private SeatGrid() {}

    public static List<String> all(Theater spec) {
        List<String> out = new ArrayList<>(spec.getCapacity());
        for (int r = 1; r <= spec.getRows(); r++) {
            String row = RowLetters.toLetters(r);
            for(int n = 1; n <= spec.getSeatsPerRow(); n++) {
                out.add(row + n);
            }
        }
        return out;
    }
}
