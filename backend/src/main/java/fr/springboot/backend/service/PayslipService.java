package fr.springboot.backend.service;

import fr.springboot.backend.model.Payslip;
import fr.springboot.backend.model.User;
import fr.springboot.backend.repository.PayslipRepository;
import fr.springboot.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service class for managing payslip operations.
 * Provides business logic for payslip creation, retrieval, and management.
 */
@Service
public class PayslipService {

    private final PayslipRepository payslipRepository;
    private final UserRepository userRepository;

    public PayslipService(PayslipRepository payslipRepository, UserRepository userRepository) {
        this.payslipRepository = payslipRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all payslips
     *
     * @return List of all payslips
     */
    public List<Payslip> findAll() {
        return payslipRepository.findAll();
    }

    /**
     * Finds all payslips for a specific user
     *
     * @param user The user
     * @return List of payslips for the user
     */
    public List<Payslip> findByUser(User user) {
        return payslipRepository.findByUser(user);
    }

    /**
     * Finds all payslips for a specific user ID
     *
     * @param id User ID
     * @return List of payslips for the user
     */
    public List<Payslip> findByUserId(Integer id) {
        return payslipRepository.findByUserId(id);
    }

    /**
     * Finds payslips with filtering options
     *
     * @param matricule User registration number filter (optional)
     * @param year Year filter (optional)
     * @param month Month filter (optional)
     * @return List of filtered payslips
     */
    public List<Payslip> findFiltered(String matricule, Integer year, Integer month) {
        return payslipRepository.findFiltered(matricule, year, month);
    }

    /**
     * Checks if a payslip already exists for a user in a specific month and year
     *
     * @param user The user
     * @param year The year
     * @param month The month (1-12)
     * @return true if payslip exists, false otherwise
     */
    public boolean existsPayslipForUserAndMonth(User user, Integer year, Integer month) {
        return payslipRepository.existsPayslipForUserAndMonth(user, year, month) > 0;
    }

    /**
     * Creates a new payslip for a user
     *
     * @param matricule User registration number
     * @param year Year of the payslip
     * @param month Month of the payslip (1-12)
     * @param bonuses Bonus amount
     * @param deductions Deduction amount
     * @return Created payslip
     * @throws IllegalArgumentException if user not found or payslip already exists
     */
    public Payslip createPayslip(String matricule, Integer year, Integer month,
                                 BigDecimal bonuses, BigDecimal deductions) {

        User user = userRepository.findByMatricule(matricule).orElseThrow();

        if (existsPayslipForUserAndMonth(user, year, month)) {
            throw new IllegalArgumentException("Une fiche de paie existe déjà pour ce mois/année");
        }

        Payslip payslip = new Payslip(year, month, bonuses, deductions, user);
        return payslipRepository.save(payslip);
    }

    /**
     * Updates an existing payslip
     *
     * @param id Payslip ID
     * @param bonuses New bonus amount
     * @param deductions New deduction amount
     * @return Updated payslip
     * @throws IllegalArgumentException if payslip not found
     */
    public Payslip updatePayslip(Integer id, BigDecimal bonuses, BigDecimal deductions) {

        Payslip payslip = payslipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fiche de paie non trouvée"));

        payslip.setBonuses(bonuses);
        payslip.setDeductions(deductions);
        payslip.calculateNetPay();

        return payslipRepository.save(payslip);
    }

    /**
     * Deletes a payslip by ID
     *
     * @param id Payslip ID to delete
     */
    public void deletePayslip(Integer id) {
        payslipRepository.deleteById(id);
    }
}