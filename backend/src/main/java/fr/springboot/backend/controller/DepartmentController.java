package fr.springboot.backend.controller;

import fr.springboot.backend.dto.DepartmentDTO;
import fr.springboot.backend.model.Department;
import fr.springboot.backend.model.User;
import fr.springboot.backend.service.DepartmentService;
import fr.springboot.backend.service.PositionService;
import fr.springboot.backend.service.UserService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

/**
 * Controller for managing departments and their associations with users
 */
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final UserService userService;

    public DepartmentController(DepartmentService deptService, UserService userService) {
        this.departmentService = deptService;
        this.userService = userService;
    }

    // -------------------- GET --------------------------

    /**
     * Retrieves all departments
     *
     * @return List of all departments
     */
    @GetMapping
    public List<Department> getAll() {
        return departmentService.findAll();
    }

    /**
     * Retrieves a department by its ID
     *
     * @param id Department ID
     * @return ResponseEntity with department or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return departmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }




    /**
     * Retrieves all users belonging to a specific department
     *
     * @param id Department ID
     * @return List of users in the department
     */
    @GetMapping("/{id}/users")
    public List<User> getUsers(@PathVariable Integer id) {
        return departmentService.getUsers(id);
    }

    /**
     * Retrieves the head of a specific department
     *
     * @param id Department ID
     * @return ResponseEntity with department head or 404 if not found
     */
    @GetMapping("/{id}/head")
    public ResponseEntity<?> getHead(@PathVariable Integer id) {
        return departmentService.findDepartmentHead(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // -------------------- POST --------------------------

    /**
     * Creates a new department
     *
     * @param department Department data to create
     * @return ResponseEntity with created department or error if code exists
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Department department) {

        if (departmentService.findByCode(department.getCode()).isPresent()) {
            return ResponseEntity.badRequest().body("Code déjà existant");
        }

        return ResponseEntity.ok(departmentService.save(department));
    }

    // -------------------- PUT --------------------------

    /**
     * Updates an existing department
     *
     * @param id Department ID to update
     * @param data Updated department data
     * @return ResponseEntity with updated department or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Department data) {
        Optional<Department> db = departmentService.findById(id);

        if (db.isEmpty()) return ResponseEntity.notFound().build();

        // Vérifie si un autre département a déjà le même code
        Optional<Department> existingWithCode = departmentService.findByCode(data.getCode());
        if (existingWithCode.isPresent() && !existingWithCode.get().getId().equals(id)) {
            return ResponseEntity.badRequest().body("Code déjà existant");
        }

        Department d = db.get();
        d.setName(data.getName());
        d.setCode(data.getCode());
        d.setDescription(data.getDescription());

        return ResponseEntity.ok(departmentService.save(d));
    }

    // -------------------- DELETE --------------------------

    /**
     * Deletes a department if it has no users
     *
     * @param id Department ID to delete
     * @return ResponseEntity with success or error if department has users
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {

        if (departmentService.countUsers(id) > 0)
            return ResponseEntity.badRequest().body("Le département contient encore des utilisateurs");

        departmentService.delete(id);
        return ResponseEntity.ok().build();
    }

    // -------------------- ASSIGN / REMOVE --------------------------

    /**
     * Assigns a user to a department
     *
     * @param id Department ID
     * @param matricule User matricule to assign
     * @return ResponseEntity with success or error message
     */
    @PostMapping("/{id}/assign")
    public ResponseEntity<?> assign(
            @PathVariable Integer id,
            @RequestParam String matricule
    ) {
        if (departmentService.assignUserToDepartment(id, matricule))
            return ResponseEntity.ok().build();

        return ResponseEntity.badRequest().body("Impossible d'assigner l'utilisateur");
    }

    /**
     * Removes a user from a department
     *
     * @param id Department ID
     * @param matricule User matricule to remove
     * @return ResponseEntity with success or error message
     */
    @PostMapping("/{id}/remove")
    public ResponseEntity<?> remove(
            @PathVariable Integer id,
            @RequestParam String matricule
    ) {
        if (departmentService.removeUserFromDepartment(id, matricule))
            return ResponseEntity.ok().build();

        return ResponseEntity.badRequest().body("Impossible de retirer l'utilisateur");
    }

    @GetMapping("/dto")
    public List<DepartmentDTO> getAllDTO() {
        return departmentService.getAllDTO();
    }

}
