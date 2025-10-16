package dk.ek.kinoxp.web.controller;

import dk.ek.kinoxp.domain.Screening;
import dk.ek.kinoxp.domain.Ticket;
import dk.ek.kinoxp.domain.Theater;           // Theater.of(auditorium) -> rows/seats/capacity
import dk.ek.kinoxp.domain.enums.TicketType;
import dk.ek.kinoxp.service.ScreeningService;
import dk.ek.kinoxp.service.TicketService;
import dk.ek.kinoxp.web.dto.SeatPurchaseForm;
import dk.ek.kinoxp.domain.RowLetters;       // A->1, AA->27 ... (fra tidligere svar)
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final ScreeningService screeningService;

    public TicketController(TicketService ticketService, ScreeningService screeningService) {
        this.ticketService = ticketService;
        this.screeningService = screeningService;
    }

    // Liste (uændret)
    @GetMapping
    public String list(Model model) {
        model.addAttribute("tickets", ticketService.getAll());
        return "tickets/list";
    }

    // Gør typer altid tilgængelige i view (også ved valideringsfejl)
    @ModelAttribute("types")
    public TicketType[] types() {
        return TicketType.values();
    }

    // Købsside for en given screening (vælg sæde)
    @GetMapping("/new")
    public String createForm(@RequestParam Long screeningId, Model model) {
        Screening s = screeningService.get(screeningId);
        var spec = Theater.of(s.getAuditorium());
        long sold = ticketService.countSold(screeningId);
        int capacity = spec.getCapacity();
        int available = capacity - (int) sold;

        SeatPurchaseForm form = new SeatPurchaseForm();
        form.setScreeningId(screeningId);

        model.addAttribute("screening", s);
        model.addAttribute("capacity", capacity);
        model.addAttribute("sold", sold);
        model.addAttribute("available", available);
        model.addAttribute("occupied", toSeatStrings(ticketService.occupiedPairs(screeningId))); // ["A1","A2",...]
        model.addAttribute("ticket", form); // bevarer navnet "ticket" i thymeleaf
        return "tickets/form";
    }

    // Gennemfør køb af ET sæde
    @PostMapping
    public String create(@Valid @ModelAttribute("ticket") SeatPurchaseForm form,
                         BindingResult br,
                         Model model,
                         RedirectAttributes ra) {

        Screening s = screeningService.get(form.getScreeningId());
        var spec = Theater.of(s.getAuditorium());

        // Server-side domænevalidering af række/sæde
        if (!br.hasFieldErrors("rowLetter")) {
            try {
                int rowIdx = RowLetters.toIndex(form.getRowLetter());
                if (rowIdx < 1 || rowIdx > spec.getRows()) {
                    br.rejectValue("rowLetter", "row.invalid", "Ugyldig række for denne sal.");
                }
            } catch (IllegalArgumentException ex) {
                br.rejectValue("rowLetter", "row.invalid", "Ugyldig række.");
            }
        }
        if (!br.hasFieldErrors("seatNumber")) {
            if (form.getSeatNumber() == null || form.getSeatNumber() < 1 || form.getSeatNumber() > spec.getSeatsPerRow()) {
                br.rejectValue("seatNumber", "seat.invalid", "Ugyldigt sædenummer for denne række.");
            }
        }

        if (br.hasErrors()) {
            enrichFormModel(model, s);
            return "tickets/form";
        }

        try {
            // sælg sædet (servicen beskytter mod dobbeltsalg via unik constraint)
            Ticket t = ticketService.sellSeat(form.getScreeningId(), form.getRowLetter(), form.getSeatNumber(), form.getType());
            ra.addFlashAttribute("msg", "Købt: " + form.getRowLetter().toUpperCase() + form.getSeatNumber());
            return "redirect:/tickets/" + t.getId();
        } catch (IllegalStateException ex) {
            // fx "Sæde A12 er netop blevet optaget"
            br.reject("seat.taken", ex.getMessage());
            enrichFormModel(model, s);
            return "tickets/form";
        }
    }

    // Kvittering
    @GetMapping("/{id}")
    public String receipt(@PathVariable Long id, Model model) {
        Ticket t = ticketService.get(id);
        model.addAttribute("ticket", t);
        return "tickets/receipt";
    }

    // JSON: optagne sæder (til et grid i UI)
    @GetMapping("/occupied")
    @ResponseBody
    public List<String> occupied(@RequestParam Long screeningId) {
        return toSeatStrings(ticketService.occupiedPairs(screeningId)); // ["A1","B3",...]
    }

    // --- helpers ---
    private static List<String> toSeatStrings(List<Object[]> tuples) {
        List<String> out = new ArrayList<>();
        for (Object[] t : tuples) {
            out.add(((String) t[0]).toUpperCase() + ((Number) t[1]).intValue());
        }
        return out;
    }

    private void enrichFormModel(Model model, Screening s) {
        var spec = Theater.of(s.getAuditorium());
        long sold = ticketService.countSold(s.getId());
        int capacity = spec.getCapacity();
        int available = capacity - (int) sold;
        model.addAttribute("screening", s);
        model.addAttribute("capacity", capacity);
        model.addAttribute("sold", sold);
        model.addAttribute("available", available);
        model.addAttribute("occupied", toSeatStrings(ticketService.occupiedPairs(s.getId())));
    }
}
