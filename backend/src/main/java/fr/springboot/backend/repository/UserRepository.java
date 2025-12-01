package fr.springboot.backend.repository;

import fr.springboot.backend.enums.Role;
import fr.springboot.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides CRUD operations and custom queries for user management.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Finds a user by their registration number
     *
     * @param matricule User registration number
     * @return Optional containing the user if found
     */
    Optional<User> findByMatricule(String matricule);

    /**
     * Finds a user by their email address
     *
     * @param email User email address
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds all users with a specific role
     *
     * @param role User role
     * @return List of users with the specified role
     */
    List<User> findByRole(Role role);

    /**
     * Gets the next value from the sequence for generating registration numbers
     *
     * @return Next sequence value
     */
    @Query(value = "SELECT nextval('emp_seq')", nativeQuery = true)
    Long getNextMatriculeSequence();
}