package dk.ek.kinoxp.domain;

public final class RowLetters {
    private RowLetters() {}

    public static String toLetters(int oneBased) {
        if (oneBased <= 0) throw new IllegalArgumentException("Index must be bigger than 1");
        StringBuilder sb = new StringBuilder();
        int n = oneBased;
        while (n > 0) {
            n--;
            sb.insert(0, (char) ('A' + n % 26));
            n /= 26;
        }
        return sb.toString();
    }


    public static int toIndex(String letters) {
        if (letters == null || letters.isBlank()) throw new IllegalArgumentException("Letters required");
        String s = letters.toUpperCase();
        int n = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < 'A' || c > 'Z') throw new IllegalArgumentException("Invalid row letters: " + s);
            n = n * 26 + (c - 'A' + 1);
        }
        return n;
    }
}
