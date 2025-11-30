package fr.springboot.backend.repository;

import fr.springboot.backend.model.Payslip;
import fr.springboot.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayslipRepository extends JpaRepository<Payslip, Integer> {

    List<Payslip> findByUser(User user);

    @Query("SELECT p FROM Payslip p WHERE p.user.id = :userId")
    List<Payslip> findByUserId(Integer userId);

    @Query("""
            SELECT COUNT(p) FROM Payslip p
            WHERE p.user = :user
              AND p.year = :year
              AND p.month = :month
          """)
    long existsPayslipForUserAndMonth(User user, int year, int month);

    @Query("""
            SELECT p FROM Payslip p
            JOIN p.user u
            WHERE (:matricule IS NULL OR u.matricule = :matricule)
              AND (:year IS NULL OR p.year = :year)
              AND (:month IS NULL OR p.month = :month)
            ORDER BY p.year DESC, p.month DESC
            """)
    List<Payslip> findFiltered(String matricule, Integer year, Integer month);
}
