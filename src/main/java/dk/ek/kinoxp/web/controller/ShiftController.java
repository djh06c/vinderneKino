package dk.ek.kinoxp.web.controller;

import dk.ek.kinoxp.domain.Shift;
import dk.ek.kinoxp.repository.EmployeeRepository;
import dk.ek.kinoxp.repository.ShiftRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/shifts")
public class ShiftController {

    private final ShiftRepository shifts;
    private final EmployeeRepository employees;

    public ShiftController(ShiftRepository shifts, EmployeeRepository employees) {
        this.shifts = shifts;
        this.employees = employees;
    }

    @GetMapping
    public String list(Model m) {
        m.addAttribute("shifts", shifts.findAll());
        return "shifts/list";
    }

    @GetMapping("/new")
    public String form(Model m) {
        m.addAttribute("shift", new Shift());
        m.addAttribute("employees", employees.findAll());
        return "shifts/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("shift") Shift s, BindingResult br, Model m) {
        if (s.getEndTime()!=null && s.getStartTime()!=null && !s.getEndTime().isAfter(s.getStartTime())) {
            br.rejectValue("endTime","time.order","Slut skal være efter start");
        }
        if (br.hasErrors()) {
            m.addAttribute("employees", employees.findAll());
            return "shifts/form";
        }
        shifts.save(s);
        return "redirect:/shifts";
    }
}
