package fr.springboot.backend.controller;

import fr.springboot.backend.model.Position;
import fr.springboot.backend.model.User;
import fr.springboot.backend.service.PositionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/positions")
public class PositionController {

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    // -------------------- GET ------------------------

    @GetMapping
    public List<Position> getAll() {
        return positionService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return positionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/users")
    public List<User> getUsers(@PathVariable Integer id) {
        return positionService.getUsersOfPosition(id);
    }

    @GetMapping("/{id}/count")
    public long getUserCount(@PathVariable Integer id) {
        return positionService.countUsers(id);
    }

    // -------------------- CREATE ------------------------

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Position position) {

        if (position.getName() == null || position.getName().isBlank()) {
            return ResponseEntity.badRequest().body("Name is required");
        }

        if (positionService.findByName(position.getName()).isPresent()) {
            return ResponseEntity.badRequest().body("Position already exists");
        }

        return ResponseEntity.ok(positionService.save(position));
    }

    // -------------------- UPDATE ------------------------

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @RequestBody Position data
    ) {
        Optional<Position> db = positionService.findById(id);
        if (db.isEmpty()) return ResponseEntity.notFound().build();

        Position p = db.get();

        p.setName(data.getName());
        p.setDescription(data.getDescription());

        return ResponseEntity.ok(positionService.save(p));
    }

    // -------------------- DELETE ------------------------

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {

        if (positionService.countUsers(id) > 0) {
            return ResponseEntity.badRequest().body("Cannot delete: position has assigned users");
        }

        Optional<Position> p = positionService.findById(id);
        if (p.isEmpty()) return ResponseEntity.notFound().build();

        if (p.get().getName().equalsIgnoreCase("Responsable RH")) {
            return ResponseEntity.badRequest().body("Cannot delete 'Responsable RH' position");
        }

        positionService.delete(id);
        return ResponseEntity.ok().build();
    }

    // -------------------- ASSIGNATION ------------------------

    @PostMapping("/{id}/assign")
    public ResponseEntity<?> assign(
            @PathVariable Integer id,
            @RequestParam String matricule
    ) {
        if (positionService.assignUser(id, matricule))
            return ResponseEntity.ok().build();

        return ResponseEntity.badRequest().body("Cannot assign user");
    }

    @PostMapping("/{id}/remove")
    public ResponseEntity<?> remove(
            @PathVariable Integer id,
            @RequestParam String matricule
    ) {
        if (positionService.removeUser(id, matricule))
            return ResponseEntity.ok().build();

        return ResponseEntity.badRequest().body("Cannot remove user");
    }
}
