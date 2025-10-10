package dk.ek.kinoxp.service;

import dk.ek.kinoxp.domain.Seat;
import dk.ek.kinoxp.domain.Theater;
import dk.ek.kinoxp.domain.enums.SeatState;
import dk.ek.kinoxp.web.dto.HoldSeatRequest;
import dk.ek.kinoxp.web.dto.SeatPurchaseForm;
import dk.ek.kinoxp.web.dto.SeatRef;
import dk.ek.kinoxp.web.dto.SeatView;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SeatServiceImpl implements SeatService {
    private Theater defaultTheater = Theater.SAL_2;


    private record Allocation(String heldBy, String status, Instant expiresAt) {} // status: HELD | SOLD
    private final Map<String, Allocation> allocs = new ConcurrentHashMap<>();

    @Override
    public List<SeatView> listSeats(long screeningId, String sessionId) {
        int rows = defaultTheater.getRows();
        int maxNo = defaultTheater.getSeatsPerRow();
        Instant now = Instant.now();

        List<SeatView> out = new ArrayList<>(rows * maxNo);
        for (int r = 1; r <= rows; r++) {
            String row = dk.ek.kinoxp.domain.RowLetters.toLetters(r);
            for (int n = 1; n <= maxNo; n++) {
                String key = key(screeningId, row, n);
                Allocation a = allocs.get(key);
                SeatState st = computeState(a, sessionId, now);
                boolean isAisle = (n == 1) || (n == maxNo);
                out.add(new SeatView(row, n, isAisle, st));
            }
        }
        out.sort(Comparator.comparing(SeatView::row).thenComparingInt(SeatView::number));
        return out;
    }

    @Override
    public void holdSeats(long screeningId, List<SeatRef> seats, String sessionId) {
        Instant expire = Instant.now().plus(Duration.ofMinutes(5));
        for (SeatRef ref : seats) {
            String key = key(screeningId, ref.row().toUpperCase(), ref.number());
            Allocation a = allocs.get(key);
            if (a != null && "SOLD".equals(a.status)) {
                throw new IllegalStateException("Seat already sold: " + ref.row() + ref.number());
            }
            allocs.put(key, new Allocation(sessionId, "HELD", expire));
        }
    }

    @Override
    public void releaseSeats(long screeningId, List<SeatRef> seats, String sessionId) {
        for (SeatRef ref : seats) {
            String key = key(screeningId, ref.row().toUpperCase(), ref.number());
            Allocation a = allocs.get(key);
            if (a != null && "HELD".equals(a.status) && Objects.equals(a.heldBy(), sessionId)) {
                allocs.remove(key);
            }
        }
    }

    @Override
    public void checkout(SeatPurchaseForm form, String sessionId) {
        Instant now = Instant.now();
        String row = form.getRowLetter().toUpperCase();
        int number = form.getSeatNumber();
        String key = key(form.getScreeningId(), row, number);

        Allocation a = allocs.get(key);
        if (a == null || !"HELD".equals(a.status) || !Objects.equals(a.heldBy(), sessionId) || a.expiresAt().isBefore(now)) {
            throw new IllegalStateException("Hold expired or invalid for seat " + row + number);
        }
        allocs.put(key, new Allocation(null, "SOLD", null));
    }

    // ---------- helpers ----------
    private static String key(long screeningId, String row, int number) {
        return screeningId + ":" + row + "-" + number;
    }

    private static SeatState computeState(Allocation a, String mySessionId, Instant now) {
        if (a == null) return SeatState.AVAILABLE;
        if ("SOLD".equals(a.status)) return SeatState.SOLD;
        boolean valid = a.expiresAt() != null && a.expiresAt().isAfter(now);
        if (!valid) return SeatState.AVAILABLE;
        return Objects.equals(a.heldBy(), mySessionId) ? SeatState.HELD_BY_ME : SeatState.HELD_OTHER;
    }
}
