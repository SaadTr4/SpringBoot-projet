package fr.springboot.backend.service;

import fr.springboot.backend.model.Department;
import fr.springboot.backend.model.User;
import fr.springboot.backend.repository.DepartmentRepository;
import fr.springboot.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public DepartmentService(DepartmentRepository deptRepo, UserRepository userRepo) {
        this.departmentRepository = deptRepo;
        this.userRepository = userRepo;
    }

    // ------------ CRUD -----------------------------------

    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    public Optional<Department> findById(Integer id) {
        return departmentRepository.findById(id);
    }

    public Optional<Department> findByName(String name) {
        return departmentRepository.findByName(name);
    }

    public Optional<Department> findByCode(String code) {
        return departmentRepository.findByCodeIgnoreCase(code);
    }

    public Department save(Department d) {
        return departmentRepository.save(d);
    }

    public void delete(Integer id) {
        departmentRepository.deleteById(id);
    }

    // ------------ Relations -----------------------------------

    public List<User> getUsers(Integer departmentId) {
        return departmentRepository.findUsersByDepartment(departmentId);
    }

    public long countUsers(Integer departmentId) {
        return departmentRepository.countUsersByDepartment(departmentId);
    }

    public Optional<User> findDepartmentHead(Integer departmentId) {
        return departmentRepository.findDepartmentHead(departmentId);
    }

    // ------------ Assignation -----------------------------------

    public boolean assignUserToDepartment(Integer deptId, String matricule) {
        Optional<User> user = userRepository.findByMatricule(matricule);
        Optional<Department> d = departmentRepository.findById(deptId);

        if (user.isEmpty() || d.isEmpty()) return false;
        if (user.get().getDepartment() != null) return false; // déjà assigné

        User u = user.get();
        u.setDepartment(d.get());
        userRepository.save(u);
        return true;
    }

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
