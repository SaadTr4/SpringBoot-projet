package fr.springboot.backend.service;

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
                                 BigDecimal bonuses, BigDecimal deductions) {

        User user = userRepository.findByMatricule(matricule).orElseThrow();

        if (existsPayslipForUserAndMonth(user, year, month)) {
            throw new IllegalArgumentException("Payslip already exists for this month/year");
        }

        Payslip payslip = new Payslip(year, month, bonuses, deductions, user);
        return payslipRepository.save(payslip);
    }

    public Payslip updatePayslip(Integer id, BigDecimal bonuses, BigDecimal deductions) {

        Payslip payslip = payslipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payslip not found"));

        payslip.setBonuses(bonuses);
        payslip.setDeductions(deductions);
        payslip.calculateNetPay();

        return payslipRepository.save(payslip);
    }

    public void deletePayslip(Integer id) {
        payslipRepository.deleteById(id);
    }
}
