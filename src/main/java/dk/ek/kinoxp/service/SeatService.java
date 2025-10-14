package dk.ek.kinoxp.service;

import dk.ek.kinoxp.web.dto.SeatPurchaseForm;
import dk.ek.kinoxp.web.dto.SeatRef;
import dk.ek.kinoxp.web.dto.SeatView;

import java.util.List;

public interface SeatService {
    List<SeatView> listSeats(long screeningId, String sessionId);
    void holdSeats(long screeningId, List<SeatRef> seats, String sessionId);
    void releaseSeats(long screeningId, List<SeatRef> seats, String sessionId);
    void checkout(SeatPurchaseForm form, String sessionId);
}
