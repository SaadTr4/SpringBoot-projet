package fr.springboot.backend.controller;

import fr.springboot.backend.dto.PayslipDTO;
import fr.springboot.backend.model.Payslip;
import fr.springboot.backend.service.PayslipService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for managing Payslip operations.
 * Provides endpoints for CRUD operations and filtering of payslips.
 */
@RestController
@RequestMapping("/api/payslips")
public class PayslipController {

    private final PayslipService payslipService;


    /**
     * Constructor with dependency injection.
     *
     * @param payslipService the payslip service
     */
    public PayslipController(PayslipService payslipService) {
        this.payslipService = payslipService;
    }

    /**
     * Retrieves all payslips.
     *
     * @return list of all payslips
     */
    @GetMapping
    public List<PayslipDTO> getAll() {
        return payslipService.findAllDTO();
    }

    /**
     * Retrieves payslips for a specific user.
     *
     * @param id the user ID
     * @return list of payslips for the user
     */
    @GetMapping("/user/{id}")
    public List<PayslipDTO> getByUserId(@PathVariable Integer id) {
        return payslipService.findByUserIdDTO(id);
    }

    /**
     * Filters payslips based on criteria.
     *
     * @param matricule the user registration number (optional)
     * @param year the year (optional)
     * @param month the month (optional)
     * @return list of filtered payslips
     */
    @GetMapping("/filter")
    public List<PayslipDTO> filter(
            @RequestParam(required = false) String matricule,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        return payslipService.findFilteredDTO(matricule, year, month);
    }


    /**
     * Creates a new payslip for a user.
     *
     * @param matricule the user registration number
     * @param year the payslip year
     * @param month the payslip month
     * @param bonuses the bonus amount
     * @param deductions the deduction amount
     * @return the created payslip
     */
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

    /**
     * Updates an existing payslip.
     *
     * @param id the payslip ID
     * @param bonuses the updated bonus amount
     * @param deductions the updated deduction amount
     * @return the updated payslip
     */
    @PutMapping("/{id}")
    public Payslip update(
            @PathVariable Integer id,
            @RequestParam BigDecimal bonuses,
            @RequestParam BigDecimal deductions
    ) {
        return payslipService.updatePayslip(id, bonuses, deductions);
    }

    /**
     * Deletes a payslip.
     *
     * @param id the payslip ID to delete
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        payslipService.deletePayslip(id);
    }
}
