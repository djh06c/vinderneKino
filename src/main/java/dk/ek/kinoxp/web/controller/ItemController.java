package dk.ek.kinoxp.web.controller;

import dk.ek.kinoxp.domain.Item;
import dk.ek.kinoxp.domain.enums.ItemCategory;
import dk.ek.kinoxp.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/items")
public class ItemController {
    private final ItemService items;

    public ItemController(ItemService items) { this.items = items; }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("items", items.getAll());
        return "items/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("item", new Item());
        model.addAttribute("categories", ItemCategory.values());
        return "items/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("item") Item item,
                         BindingResult br,
                         Model model) {
        if (br.hasErrors()) {
            model.addAttribute("categories", ItemCategory.values());
            return "items/form";
        }
        items.save(item);
        return "redirect:/items";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("item", items.get(id));
        model.addAttribute("categories", ItemCategory.values());
        return "items/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("item") Item item,
                         BindingResult br,
                         Model model) {
        if (br.hasErrors()) {
            model.addAttribute("categories", ItemCategory.values());
            return "items/form";
        }
        item.setId(id);
        items.save(item);
        return "redirect:/items";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        items.delete(id);
        return "redirect:/items";
    }
}
