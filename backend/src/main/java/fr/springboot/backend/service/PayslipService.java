package fr.springboot.backend.service;

import fr.springboot.backend.dto.PayslipDTO;
import fr.springboot.backend.model.Payslip;
import fr.springboot.backend.model.User;
import fr.springboot.backend.repository.PayslipRepository;
import fr.springboot.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PayslipService {

    private final PayslipRepository payslipRepository;
    private final UserRepository userRepository;

    public PayslipService(PayslipRepository payslipRepository, UserRepository userRepository) {
        this.payslipRepository = payslipRepository;
        this.userRepository = userRepository;
    }

    public List<Payslip> findAll() {
        return payslipRepository.findAll();
    }

    public List<Payslip> findByUser(User user) {
        return payslipRepository.findByUser(user);
    }

    public List<Payslip> findByUserId(Integer id) {
        return payslipRepository.findByUserId(id);
    }

    public List<Payslip> findFiltered(String matricule, Integer year, Integer month) {
        return payslipRepository.findFiltered(matricule, year, month);
    }

    public boolean existsPayslipForUserAndMonth(User user, Integer year, Integer month) {
        return payslipRepository.existsPayslipForUserAndMonth(user, year, month) > 0;
    }

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

    public Payslip updatePayslip(Integer id, BigDecimal bonuses, BigDecimal customDeductions) {

        Payslip payslip = payslipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fiche de paie non trouvée"));

        if (bonuses != null) payslip.setBonuses(bonuses);
        if (customDeductions != null) payslip.setCustom_deductions(customDeductions);

        // Recalcul des déductions et du net pay
        payslip.calculateDeductions();
        payslip.calculateNetPay();

        return payslipRepository.save(payslip);
    }

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


