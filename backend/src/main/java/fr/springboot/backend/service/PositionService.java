package fr.springboot.backend.service;

import fr.springboot.backend.dto.PositionDTO;
import fr.springboot.backend.model.Position;
import fr.springboot.backend.model.User;
import fr.springboot.backend.repository.PositionRepository;
import fr.springboot.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing position operations.
 * Provides business logic for position management and user assignments.
 */
@Service
public class PositionService {

    private final PositionRepository positionRepository;
    private final UserRepository userRepository;

    public PositionService(PositionRepository positionRepository, UserRepository userRepository) {
        this.positionRepository = positionRepository;
        this.userRepository = userRepository;
    }

    // ------------------ CRUD ----------------------

    /**
     * Retrieves all positions
     *
     * @return List of all positions
     */
    public List<Position> findAll() {
        return positionRepository.findAll();
    }

    /**
     * Finds a position by its ID
     *
     * @param id Position ID
     * @return Optional containing the position if found
     */
    public Optional<Position> findById(Integer id) {
        return positionRepository.findById(id);
    }

    /**
     * Finds a position by its name
     *
     * @param name Position name
     * @return Optional containing the position if found
     */
    public Optional<Position> findByName(String name) {
        return positionRepository.findByNameIgnoreCase(name);
    }

    /**
     * Saves a position (create or update)
     *
     * @param position Position to save
     * @return Saved position
     */
    public Position save(Position position) {
        return positionRepository.save(position);
    }

    /**
     * Deletes a position by ID
     *
     * @param id Position ID to delete
     */
    public void delete(Integer id) {
        positionRepository.deleteById(id);
    }

    // ------------------ RELATIONS ----------------------

    /**
     * Retrieves all users holding a specific position
     *
     * @param positionId Position ID
     * @return List of users with the specified position
     */
    public List<User> getUsersOfPosition(Integer positionId) {
        return positionRepository.findUsersByPosition(positionId);
    }

    /**
     * Counts the number of users holding a specific position
     *
     * @param positionId Position ID
     * @return Number of users with the specified position
     */
    public long countUsers(Integer positionId) {
        return positionRepository.countUsersByPosition(positionId);
    }

    // ------------------ ASSIGNMENT ----------------------

    /**
     * Assigns a user to a position
     *
     * @param positionId Position ID
     * @param matricule User registration number
     * @return true if assignment successful, false otherwise
     */
    public boolean assignUser(Integer positionId, String matricule) {
        Optional<Position> pos = positionRepository.findById(positionId);
        Optional<User> user = userRepository.findByMatricule(matricule);

        if (pos.isEmpty() || user.isEmpty()) return false;

        User u = user.get();
        u.setPosition(pos.get());
        userRepository.save(u);
        return true;
    }

    /**
     * Removes a user from a position
     *
     * @param positionId Position ID
     * @param matricule User registration number
     * @return true if removal successful, false otherwise
     */
    public boolean removeUser(Integer positionId, String matricule) {
        Optional<User> user = userRepository.findByMatricule(matricule);

        if (user.isEmpty()) return false;

        User u = user.get();

        if (u.getPosition() == null || !u.getPosition().getId().equals(positionId))
            return false;

        u.setPosition(null);
        userRepository.save(u);
        return true;
    }

    public List<PositionDTO> findAllDTO() {
        return positionRepository.findAll()
                .stream()
                .map(PositionDTO::new)
                .toList();
    }

}