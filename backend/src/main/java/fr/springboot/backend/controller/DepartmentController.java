package fr.springboot.backend.controller;

import fr.springboot.backend.model.Department;
import fr.springboot.backend.model.User;
import fr.springboot.backend.service.DepartmentService;
import fr.springboot.backend.service.UserService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

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

    @GetMapping
    public List<Department> getAll() {
        return departmentService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return departmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/users")
    public List<User> getUsers(@PathVariable Integer id) {
        return departmentService.getUsers(id);
    }

    @GetMapping("/{id}/head")
    public ResponseEntity<?> getHead(@PathVariable Integer id) {
        return departmentService.findDepartmentHead(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // -------------------- POST --------------------------

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Department department) {

        if (departmentService.findByCode(department.getCode()).isPresent()) {
            return ResponseEntity.badRequest().body("Code already exists");
        }

        return ResponseEntity.ok(departmentService.save(department));
    }

    // -------------------- PUT --------------------------

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Department data) {
        Optional<Department> db = departmentService.findById(id);

        if (db.isEmpty()) return ResponseEntity.notFound().build();

        Department d = db.get();
        d.setName(data.getName());
        d.setCode(data.getCode());
        d.setDescription(data.getDescription());

        return ResponseEntity.ok(departmentService.save(d));
    }

    // -------------------- DELETE --------------------------

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {

        if (departmentService.countUsers(id) > 0)
            return ResponseEntity.badRequest().body("Department still has users");

        departmentService.delete(id);
        return ResponseEntity.ok().build();
    }

    // -------------------- ASSIGN / REMOVE --------------------------

    @PostMapping("/{id}/assign")
    public ResponseEntity<?> assign(
            @PathVariable Integer id,
            @RequestParam String matricule
    ) {
        if (departmentService.assignUserToDepartment(id, matricule))
            return ResponseEntity.ok().build();

        return ResponseEntity.badRequest().body("Cannot assign user");
    }

    @PostMapping("/{id}/remove")
    public ResponseEntity<?> remove(
            @PathVariable Integer id,
            @RequestParam String matricule
    ) {
        if (departmentService.removeUserFromDepartment(id, matricule))
            return ResponseEntity.ok().build();

        return ResponseEntity.badRequest().body("Cannot remove user");
    }
}
