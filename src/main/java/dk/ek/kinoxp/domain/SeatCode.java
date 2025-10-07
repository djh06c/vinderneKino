package dk.ek.kinoxp.domain;

import java.util.ArrayList;
import java.util.List;

public final class SeatCode {
    private SeatCode() {}

    public static Seat parse(String text) {
        if (text == null) throw new IllegalArgumentException("Seat text required");
        String s = text.trim().toUpperCase().replaceAll("\\s+", "");
        var m = s.matches("^[A-Z]+-?\\d+$");
        if (!m) throw new IllegalArgumentException("Invalid seat format: " + text);
        int splitAt = s.lastIndexOf('-');
        String rowLetters, numberPart;
        if (splitAt != -1) {
            rowLetters = s.substring(0, splitAt);
            numberPart = s.substring(splitAt + 1);
        } else {
            // del mellem sidste bogstav og første tal
            int i = 0; while (i < s.length() && Character.isLetter(s.charAt(i))) i++;
            rowLetters = s.substring(0, i);
            numberPart = s.substring(i);
        }
        int num = Integer.parseInt(numberPart);
        return new Seat(rowLetters, num);
    }

    public static List<Seat> parseList(String csv) {
        var out = new ArrayList<Seat>();
        if (csv == null || csv.isBlank()) return out;
        for (String part : csv.split(",")) out.add(parse(part));
        return out;
    }
}
