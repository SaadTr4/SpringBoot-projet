package fr.springboot.backend.service;

import fr.springboot.backend.dto.PayslipDTO;
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
     * @param customDeductions Deduction amount
     * @return Created payslip
     * @throws IllegalArgumentException if user not found or payslip already exists
     */
    public Payslip createPayslip(String matricule, Integer year, Integer month,
                                 BigDecimal bonuses, BigDecimal customDeductions) {

        User user = userRepository.findByMatricule(matricule)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        if (existsPayslipForUserAndMonth(user, year, month)) {
            throw new IllegalArgumentException("Fiche de paie déjà existante pour ce mois/année");
        }

        Payslip payslip = new Payslip(year, month, bonuses, customDeductions, user);

        // Assure que les montants sont bien calculés
        payslip.calculateDeductions();
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

    // ========================
    // Conversion vers DTO
    // ========================
    public PayslipDTO toDTO(Payslip p) {
        // S'assurer que tous les champs sont initialisés
        if (p.getBaseSalary() == null) {
            p.setBaseSalary(p.getUser() != null && p.getUser().getBaseSalary() != null
                    ? p.getUser().getBaseSalary()
                    : BigDecimal.ZERO);
        }
        if (p.getBonuses() == null) p.setBonuses(BigDecimal.ZERO);
        if (p.getCustom_deductions() == null) p.setCustom_deductions(BigDecimal.ZERO);

        // Recalculer les déductions et le net pay
        p.calculateDeductions();
        p.calculateNetPay();

        String nom = p.getUser() != null
                ? p.getUser().getFirstName() + " " + p.getUser().getLastName()
                : "";

        PayslipDTO dto = new PayslipDTO(
                p.getId(),
                p.getMonth(),
                p.getYear(),
                p.getBaseSalary(),
                p.getBonuses(),
                p.getDeductions(),
                p.getNetPay(),
                p.getCustom_deductions(),
                nom
        );

        // Remplir le monthName
        dto.setMonthName(p.getMonth());

        return dto;

    }

    /**
     * Updates an existing payslip
     *
     * @param id Payslip ID
     * @param bonuses New bonus amount
     * @param customDeductions New deduction amount
     * @return Updated payslip
     * @throws IllegalArgumentException if payslip not found
     */
    public Payslip updatePayslip(Integer id, BigDecimal bonuses, BigDecimal customDeductions,
                                 Integer month, Integer year) {  // ← Ajouter month et year
        Payslip payslip = payslipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fiche de paie non trouvée"));

        System.out.println("AVANT UPDATE: " + payslip);  // DEBUG

        if (bonuses != null) payslip.setBonuses(bonuses);
        if (customDeductions != null) payslip.setCustom_deductions(customDeductions);
        if (month != null) payslip.setMonth(month);  // ← Ajouter ici
        if (year != null) payslip.setYear(year);    // ← Ajouter ici

        // Recalcule des déductions et du net pay
        payslip.calculateDeductions();
        payslip.calculateNetPay();

        Payslip saved = payslipRepository.save(payslip);
        System.out.println("APRÈS UPDATE: " + saved);  // DEBUG

        return saved;
    }

    public List<PayslipDTO> findAllDTO() {
        return findAll().stream()
                .map(this::toDTO)
                .toList();
    }
    public PayslipDTO findByIdDTO(Integer id) {
        Payslip p = payslipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fiche introuvable"));
        return toDTO(p);
    }

    public List<PayslipDTO> findByUserIdDTO(Integer id) {
        return findByUserId(id).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<PayslipDTO> findFilteredDTO(String matricule, Integer year, Integer month) {
        return findFiltered(matricule, year, month).stream()
                .map(this::toDTO)
                .toList();
    }
}


