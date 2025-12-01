package fr.springboot.backend.controller;

import fr.springboot.backend.dto.PositionDTO;
import fr.springboot.backend.model.Position;
import fr.springboot.backend.model.User;
import fr.springboot.backend.service.PositionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing Position operations.
 * Provides endpoints for CRUD operations and user assignments for positions.
 */
@RestController
@RequestMapping("/api/positions")
@CrossOrigin(origins = "http://localhost:4200")
public class PositionController {

    private final PositionService positionService;

    /**
     * Constructor with dependency injection.
     *
     * @param positionService the position service
     */
    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    // -------------------- GET ENDPOINTS ------------------------

    /**
     * Retrieves all positions.
     *
     * @return list of all positions
     */
    @GetMapping
    public List<PositionDTO> getAll() {
        return positionService.findAll().stream()
                .map(PositionDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a position by its ID.
     *
     * @param id the position ID
     * @return ResponseEntity with position if found, 404 otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return positionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves all users holding a specific position.
     *
     * @param id the position ID
     * @return list of users with the position
     */
    @GetMapping("/{id}/users")
    public List<User> getUsers(@PathVariable Integer id) {
        return positionService.getUsersOfPosition(id);
    }

    /**
     * Gets the count of users holding a specific position.
     *
     * @param id the position ID
     * @return the number of users with the position
     */
    @GetMapping("/{id}/count")
    public long getUserCount(@PathVariable Integer id) {
        return positionService.countUsers(id);
    }

    // -------------------- CREATE ENDPOINTS ------------------------

    /**
     * Creates a new position.
     *
     * @param position the position to create
     * @return ResponseEntity with created position or error message
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Position position) {

        if (position.getName() == null || position.getName().isBlank()) {
            return ResponseEntity.badRequest().body("Le nom du poste est requis");
        }

        if (positionService.findByName(position.getName()).isPresent()) {
            return ResponseEntity.badRequest().body("Un poste avec ce nom existe déjà");
        }

        return ResponseEntity.ok(positionService.save(position));
    }

    // -------------------- UPDATE ENDPOINTS ------------------------

    /**
     * Updates an existing position.
     *
     * @param id the position ID to update
     * @param data the updated position data
     * @return ResponseEntity with updated position or 404 if not found
     */
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

    // -------------------- DELETE ENDPOINTS ------------------------

    /**
     * Deletes a position if it has no users and is not the "Responsable RH" position.
     *
     * @param id the position ID to delete
     * @return ResponseEntity with success or error message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {

        if (positionService.countUsers(id) > 0) {
            return ResponseEntity.badRequest().body("Impossible de supprimer un poste avec des utilisateurs assignés");
        }

        Optional<Position> p = positionService.findById(id);
        if (p.isEmpty()) return ResponseEntity.notFound().build();

        if (p.get().getName().equalsIgnoreCase("Responsable RH")) {
            return ResponseEntity.badRequest().body("Impossible de supprimer le poste 'Responsable RH'");
        }

        positionService.delete(id);
        return ResponseEntity.ok().build();
    }

    // -------------------- ASSIGNMENT ENDPOINTS ------------------------

    /**
     * Assigns a user to a position.
     *
     * @param id the position ID
     * @param matricule the user's registration number
     * @return ResponseEntity with success or error message
     */
    @PostMapping("/{id}/assign")
    public ResponseEntity<?> assign(
            @PathVariable Integer id,
            @RequestParam String matricule
    ) {
        if (positionService.assignUser(id, matricule))
            return ResponseEntity.ok().build();

        return ResponseEntity.badRequest().body("Impossible d'assigner l'utilisateur au poste");
    }

    /**
     * Removes a user from a position.
     *
     * @param id the position ID
     * @param matricule the user's registration number
     * @return ResponseEntity with success or error message
     */
    @PostMapping("/{id}/remove")
    public ResponseEntity<?> remove(
            @PathVariable Integer id,
            @RequestParam String matricule
    ) {
        if (positionService.removeUser(id, matricule))
            return ResponseEntity.ok().build();

        return ResponseEntity.badRequest().body("Impossible de retirer l'utilisateur du poste");
    }
    @GetMapping("/dto")
    public List<PositionDTO> getAllPositionDTO() {
        return positionService.findAllDTO();
    }
}
