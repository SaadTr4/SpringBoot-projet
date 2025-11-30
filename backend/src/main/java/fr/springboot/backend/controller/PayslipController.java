package fr.springboot.backend.controller;

import fr.springboot.backend.model.Payslip;
import fr.springboot.backend.service.PayslipService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payslips")
public class PayslipController {

    private final PayslipService payslipService;

    public PayslipController(PayslipService payslipService) {
        this.payslipService = payslipService;
    }

    @GetMapping
    public List<Payslip> getAll() {
        return payslipService.findAll();
    }

    @GetMapping("/user/{id}")
    public List<Payslip> getByUserId(@PathVariable Integer id) {
        return payslipService.findByUserId(id);
    }

    @GetMapping("/filter")
    public List<Payslip> filter(
            @RequestParam(required = false) String matricule,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        return payslipService.findFiltered(matricule, year, month);
    }

    @PostMapping("/create")
    public Payslip create(
            @RequestParam String matricule,
            @RequestParam Integer year,
            @RequestParam Integer month,
            @RequestParam BigDecimal bonuses,
            @RequestParam BigDecimal deductions
    ) {
        return payslipService.createPayslip(matricule, year, month, bonuses, deductions);
    }

    @PutMapping("/{id}")
    public Payslip update(
            @PathVariable Integer id,
            @RequestParam BigDecimal bonuses,
            @RequestParam BigDecimal deductions
    ) {
        return payslipService.updatePayslip(id, bonuses, deductions);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        payslipService.deletePayslip(id);
    }
}
