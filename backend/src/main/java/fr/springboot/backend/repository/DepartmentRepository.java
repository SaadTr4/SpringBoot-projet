package fr.springboot.backend.repository;

import fr.springboot.backend.model.Department;
import fr.springboot.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Department entity.
 * Provides CRUD operations and custom queries for department management.
 */
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    /**
     * Finds a department by its name
     *
     * @param name Department name
     * @return Optional containing the department if found
     */
    Optional<Department> findByName(String name);

    /**
     * Finds a department by its code (case-insensitive)
     *
     * @param code Department code
     * @return Optional containing the department if found
     */
    Optional<Department> findByCodeIgnoreCase(String code);

    /**
     * Finds all users belonging to a specific department
     *
     * @param departmentId Department ID
     * @return List of users in the department
     */
    @Query("SELECT u FROM User u WHERE u.department.id = :departmentId")
    List<User> findUsersByDepartment(Integer departmentId);

    /**
     * Counts the number of users in a specific department
     *
     * @param departmentId Department ID
     * @return Number of users in the department
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.department.id = :departmentId")
    long countUsersByDepartment(Integer departmentId);

    /**
     * Finds the department head (CHEF_DEPARTEMENT) for a specific department
     *
     * @param departmentId Department ID
     * @return Optional containing the department head if found
     */
    @Query("SELECT u FROM User u WHERE u.department.id = :departmentId AND u.role = fr.springboot.backend.enums.Role.CHEF_DEPARTEMENT")
    Optional<User> findDepartmentHead(Integer departmentId);
}
