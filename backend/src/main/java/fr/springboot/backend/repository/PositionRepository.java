package fr.springboot.backend.repository;

import fr.springboot.backend.model.Position;
import fr.springboot.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Position entity.
 * Provides CRUD operations and custom queries for position management.
 */
public interface PositionRepository extends JpaRepository<Position, Integer> {

    /**
     * Finds a position by its name (case-insensitive)
     *
     * @param name Position name
     * @return Optional containing the position if found
     */
    Optional<Position> findByNameIgnoreCase(String name);

    /**
     * Finds all users holding a specific position
     *
     * @param positionId Position ID
     * @return List of users with the specified position
     */
    @Query("SELECT u FROM User u WHERE u.position.id = :positionId")
    List<User> findUsersByPosition(@Param("positionId") Integer positionId);

    /**
     * Counts the number of users holding a specific position
     *
     * @param positionId Position ID
     * @return Number of users with the specified position
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.position.id = :positionId")
    long countUsersByPosition(@Param("positionId") Integer positionId);

    /**
     * Finds a user by their registration number
     *
     * @param matricule User registration number
     * @return Optional containing the user if found
     */
    @Query("SELECT u FROM User u WHERE u.matricule = :matricule")
    Optional<User> findUserByMatricule(@Param("matricule") String matricule);
}