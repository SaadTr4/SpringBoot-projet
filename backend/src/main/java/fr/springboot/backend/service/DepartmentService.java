package fr.springboot.backend.service;

import fr.springboot.backend.model.Department;
import fr.springboot.backend.model.User;
import fr.springboot.backend.repository.DepartmentRepository;
import fr.springboot.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing department operations.
 * Provides business logic for department management and user assignments.
 */
@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public DepartmentService(DepartmentRepository deptRepo, UserRepository userRepo) {
        this.departmentRepository = deptRepo;
        this.userRepository = userRepo;
    }

    // ------------ CRUD -----------------------------------

    /**
     * Retrieves all departments
     *
     * @return List of all departments
     */
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    /**
     * Finds a department by its ID
     *
     * @param id Department ID
     * @return Optional containing the department if found
     */
    public Optional<Department> findById(Integer id) {
        return departmentRepository.findById(id);
    }

    /**
     * Finds a department by its name
     *
     * @param name Department name
     * @return Optional containing the department if found
     */
    public Optional<Department> findByName(String name) {
        return departmentRepository.findByName(name);
    }

    /**
     * Finds a department by its code
     *
     * @param code Department code
     * @return Optional containing the department if found
     */
    public Optional<Department> findByCode(String code) {
        return departmentRepository.findByCodeIgnoreCase(code);
    }

    /**
     * Saves a department (create or update)
     *
     * @param d Department to save
     * @return Saved department
     */
    public Department save(Department d) {
        return departmentRepository.save(d);
    }

    /**
     * Deletes a department by ID
     *
     * @param id Department ID to delete
     */
    public void delete(Integer id) {
        departmentRepository.deleteById(id);
    }

    // ------------ Relations -----------------------------------

    /**
     * Retrieves all users in a specific department
     *
     * @param departmentId Department ID
     * @return List of users in the department
     */
    public List<User> getUsers(Integer departmentId) {
        return departmentRepository.findUsersByDepartment(departmentId);
    }

    /**
     * Counts the number of users in a specific department
     *
     * @param departmentId Department ID
     * @return Number of users in the department
     */
    public long countUsers(Integer departmentId) {
        return departmentRepository.countUsersByDepartment(departmentId);
    }

    /**
     * Finds the department head for a specific department
     *
     * @param departmentId Department ID
     * @return Optional containing the department head if found
     */
    public Optional<User> findDepartmentHead(Integer departmentId) {
        return departmentRepository.findDepartmentHead(departmentId);
    }

    // ------------ Assignment -----------------------------------

    /**
     * Assigns a user to a department
     *
     * @param deptId Department ID
     * @param matricule User registration number
     * @return true if assignment successful, false otherwise
     */
    public boolean assignUserToDepartment(Integer deptId, String matricule) {
        Optional<User> user = userRepository.findByMatricule(matricule);
        Optional<Department> d = departmentRepository.findById(deptId);

        if (user.isEmpty() || d.isEmpty()) return false;
        if (user.get().getDepartment() != null) return false; // already assigned

        User u = user.get();
        u.setDepartment(d.get());
        userRepository.save(u);
        return true;
    }

    /**
     * Removes a user from a department
     *
     * @param deptId Department ID
     * @param matricule User registration number
     * @return true if removal successful, false otherwise
     */
    public boolean removeUserFromDepartment(Integer deptId, String matricule) {
        Optional<User> user = userRepository.findByMatricule(matricule);

        if (user.isEmpty()) return false;

        User u = user.get();

        if (u.getDepartment() == null || !u.getDepartment().getId().equals(deptId))
            return false;

        u.setDepartment(null);
        userRepository.save(u);
        return true;
    }
}