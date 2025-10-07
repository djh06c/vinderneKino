package dk.ek.kinoxp.web.controller;

import dk.ek.kinoxp.domain.Screening;
import dk.ek.kinoxp.domain.Ticket;
import dk.ek.kinoxp.domain.enums.TicketType;
import dk.ek.kinoxp.service.ScreeningService;
import dk.ek.kinoxp.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public String list(Model model) {
        model.addAttribute("tickets", ticketService.getAll());
        return "tickets/list";
    }

    @GetMapping("/new")
    public String createForm(@RequestParam Long screeningId, Model model) {
        Screening s = screeningService.get(screeningId);
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("screening", s);
        model.addAttribute("types", TicketType.values());
        return "tickets/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("ticket") Ticket ticket, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("types", TicketType.values());
            return "tickets/form";
        }
        ticketService.sellTicket(ticket);
        return "redirect:/screenings?movieId=" + ticket.getScreening().getMovie().getId();
    }
}
