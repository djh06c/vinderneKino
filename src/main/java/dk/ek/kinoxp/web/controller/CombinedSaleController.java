package dk.ek.kinoxp.web.controller;

import dk.ek.kinoxp.domain.enums.TicketType;
import dk.ek.kinoxp.service.CombinedSaleService;
import dk.ek.kinoxp.service.ItemService;
import dk.ek.kinoxp.service.ScreeningService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/combined-sale")
public class CombinedSaleController {

    private final CombinedSaleService combinedSaleService;
    private final ScreeningService screeningService;
    private final ItemService itemService;

    public CombinedSaleController(CombinedSaleService combinedSaleService,
                                  ScreeningService screeningService,
                                  ItemService itemService) {
        this.combinedSaleService = combinedSaleService;
        this.screeningService = screeningService;
        this.itemService = itemService;
    }

    @GetMapping
    public String form(@RequestParam Long screeningId, Model model) {
        model.addAttribute("screening", screeningService.getById(screeningId));
        model.addAttribute("ticketTypes", TicketType.values());
        model.addAttribute("items", itemService.getAll());
        return "combinedSale/form";
    }

    @PostMapping
    public String register(@RequestParam Long screeningId,
                           @RequestParam Map<String, String> params,
                           Model model) {

        Map<TicketType, Integer> ticketQuantities = new EnumMap<>(TicketType.class);
        Map<Long, Integer> itemQuantities = new HashMap<>();

        params.forEach((k, v) -> {
            if (v.isBlank()) return;
            try {
                int val = Integer.parseInt(v);
                if (k.startsWith("ticket_")) {
                    TicketType type = TicketType.valueOf(k.substring(7));
                    ticketQuantities.put(type, val);
                } else if (k.startsWith("item_")) {
                    Long id = Long.parseLong(k.substring(5));
                    itemQuantities.put(id, val);
                }
            } catch (Exception ignored) {}
        });

        combinedSaleService.registerCombinedSale(screeningId, ticketQuantities, itemQuantities);
        return "redirect:/screenings";
    }
}
