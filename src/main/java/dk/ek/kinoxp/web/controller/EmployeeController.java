package dk.ek.kinoxp.web.controller;

import dk.ek.kinoxp.domain.Employee;
import dk.ek.kinoxp.repository.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeRepository repo;
    public EmployeeController(EmployeeRepository repo){ this.repo = repo; }

    @GetMapping
    public String list(Model m){ m.addAttribute("employees", repo.findAll()); return "employees/list"; }

    @GetMapping("/new")
    public String form(Model m){ m.addAttribute("employee", new Employee()); return "employees/form"; }

    @PostMapping
    public String create(@Valid @ModelAttribute("employee") Employee e, BindingResult br){
        if (br.hasErrors()) return "employees/form";
        repo.save(e); return "redirect:/employees";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model m){
        m.addAttribute("employee", repo.findById(id).orElseThrow()); return "employees/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("employee") Employee e, BindingResult br){
        if (br.hasErrors()) return "employees/form";
        e.setId(id); repo.save(e); return "redirect:/employees";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id){ repo.deleteById(id); return "redirect:/employees"; }
}
