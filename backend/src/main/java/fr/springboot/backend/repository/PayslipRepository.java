package fr.springboot.backend.repository;

import fr.springboot.backend.model.Payslip;
import fr.springboot.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Payslip entity.
 * Provides CRUD operations and custom queries for payslip management.
 */
@Repository
public interface PayslipRepository extends JpaRepository<Payslip, Integer> {

    /**
     * Finds all payslips for a specific user
     *
     * @param user The user
     * @return List of payslips for the user
     */
    List<Payslip> findByUser(User user);

    /**
     * Finds all payslips for a specific user ID
     *
     * @param userId The user ID
     * @return List of payslips for the user
     */
    @Query("SELECT p FROM Payslip p WHERE p.user.id = :userId")
    List<Payslip> findByUserId(Integer userId);

    /**
     * Checks if a payslip already exists for a user in a specific month and year
     *
     * @param user The user
     * @param year The year
     * @param month The month (1-12)
     * @return Number of existing payslips (0 or 1)
     */
    @Query("SELECT COUNT(p) FROM Payslip p WHERE p.user = :user AND p.year = :year AND p.month = :month")
    long existsPayslipForUserAndMonth(User user, int year, int month);


    /**
     * Finds payslips with filtering options
     *
     * @param matricule User registration number (optional)
     * @param year Year filter (optional)
     * @param month Month filter (optional)
     * @return List of filtered payslips ordered by year and month descending
     */
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
