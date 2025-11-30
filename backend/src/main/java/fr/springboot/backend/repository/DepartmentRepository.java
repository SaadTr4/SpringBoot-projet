package fr.springboot.backend.repository;

import fr.springboot.backend.model.Department;
import fr.springboot.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    Optional<Department> findByName(String name);

    Optional<Department> findByCodeIgnoreCase(String code);

    @Query("SELECT u FROM User u WHERE u.department.id = :departmentId")
    List<User> findUsersByDepartment(Integer departmentId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.department.id = :departmentId")
    long countUsersByDepartment(Integer departmentId);

    @Query("SELECT u FROM User u WHERE u.department.id = :departmentId AND u.role = fr.springboot.backend.enums.Role.CHEF_DEPARTEMENT")
    Optional<User> findDepartmentHead(Integer departmentId);
}
