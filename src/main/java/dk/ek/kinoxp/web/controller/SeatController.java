package dk.ek.kinoxp.web.controller;

import dk.ek.kinoxp.service.SeatService;
import dk.ek.kinoxp.web.dto.HoldSeatRequest;
import dk.ek.kinoxp.web.dto.SeatPurchaseForm;
import dk.ek.kinoxp.web.dto.SeatView;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping("/screenings/{id}/seats")
    public List<SeatView> seats(@PathVariable("id") long screeningId, HttpSession session) {
        return seatService.listSeats(screeningId, session.getId());
    }

    @PostMapping("/screenings/{id}/hold")
    public ResponseEntity<?> hold(@PathVariable("id") long screeningId,
                                  @RequestBody HoldSeatRequest req,
                                  HttpSession session) {
        seatService.holdSeats(screeningId, req.seats(), session.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/screenings/{id}/release")
    public ResponseEntity<?> release(@PathVariable("id") long screeningId,
                                     @RequestBody HoldSeatRequest req,
                                     HttpSession session) {
        seatService.releaseSeats(screeningId, req.seats(), session.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/screenings/checkout")
    public ResponseEntity<?> checkout(@RequestBody SeatPurchaseForm form, HttpSession session) {
        seatService.checkout(form, session.getId());
        return ResponseEntity.ok().build();
    }
}
