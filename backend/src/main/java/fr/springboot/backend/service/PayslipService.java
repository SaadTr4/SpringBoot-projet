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
        payslip.setRegistrationNumber(user.getMatricule());

        // Assure que les montants sont bien calculés
        payslip.calculateDeductions();
        payslip.calculateNetPay();

        return payslipRepository.save(payslip);
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
    public Payslip updatePayslip(Integer id, BigDecimal bonuses, BigDecimal customDeductions) {

        Payslip payslip = payslipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fiche de paie non trouvée"));

        if (bonuses != null) payslip.setBonuses(bonuses);
        if (customDeductions != null) payslip.setCustom_deductions(customDeductions);

        // Recalcule des déductions et du net pay.
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
    private PayslipDTO toDTO(Payslip p) {
        // recalcul si nécessaire
        if (p.getBaseSalary() == null) p.setBaseSalary(p.getUser() != null && p.getUser().getBaseSalary() != null ? p.getUser().getBaseSalary() : BigDecimal.ZERO);
        if (p.getBonuses() == null) p.setBonuses(BigDecimal.ZERO);
        if (p.getDeductions() == null) p.setDeductions(BigDecimal.ZERO);

        // recalcul net pay
        p.calculateNetPay();

        String nom = p.getUser() != null
                ? p.getUser().getFirstName() + " " + p.getUser().getLastName()
                : "";

        return new PayslipDTO(
                p.getId(),
                p.getMonth(),
                p.getYear(),
                p.getBaseSalary(),
                p.getBonuses(),
                p.getDeductions(),
                nom
        );
    }


    public List<PayslipDTO> findAllDTO() {
        return findAll().stream()
                .map(this::toDTO)
                .toList();
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


