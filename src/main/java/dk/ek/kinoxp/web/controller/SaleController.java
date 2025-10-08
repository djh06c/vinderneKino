package dk.ek.kinoxp.web.controller;

import dk.ek.kinoxp.domain.Sale;
import dk.ek.kinoxp.service.ItemService;
import dk.ek.kinoxp.service.SaleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/sales")
public class SaleController {

    private final SaleService saleService;
    private final ItemService itemService;

    public SaleController(SaleService saleService, ItemService itemService) {
        this.saleService = saleService;
        this.itemService = itemService;
    }

    // Liste over salg
    @GetMapping
    public String list(Model model) {
        model.addAttribute("sales", saleService.getAll());
        return "sales/list";
    }

    // Nyttigt skema til at registrere salg
    @GetMapping("/new")
    public String newSaleForm(Model model) {
        model.addAttribute("items", itemService.getAll());
        return "sales/form";
    }

    // Registrér salg
    @PostMapping
    public String registerSale(@RequestParam Map<String, String> params,
                               RedirectAttributes ra) {
        Map<Long, Integer> quantities = new HashMap<>();

        // Forventede feltnavne i form: qty_<itemId> (fx qty_5 = 2)
        for (Map.Entry<String, String> e : params.entrySet()) {
            String key = e.getKey();
            String val = e.getValue();
            if (!key.startsWith("qty_")) continue;
            if (val == null || val.isBlank()) continue;

            try {
                long itemId = Long.parseLong(key.substring(4));
                int qty = Integer.parseInt(val);
                if (qty > 0) {
                    quantities.put(itemId, qty);
                }
            } catch (NumberFormatException ignore) { /* ignorer ugyldige felter */ }
        }

        try {
            Sale sale = saleService.registerSale(quantities);
            ra.addFlashAttribute("message",
                    "Salg #" + sale.getId() + " registreret (" + sale.getTotal() + " kr).");
            return "redirect:/sales";
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/sales/new";
        }
    }
}
