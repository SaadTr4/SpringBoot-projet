package fr.springboot.backend.repository;

import fr.springboot.backend.model.Project;
import fr.springboot.backend.model.User;
import fr.springboot.backend.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Project entity.
 * Provides CRUD operations and custom queries for project management.
 */
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    /**
     * Finds a project by its name
     *
     * @param name Project name
     * @return Optional containing the project if found
     */
    Optional<Project> findByName(String name);

    /**
     * Finds all projects with a specific status
     *
     * @param status Project status
     * @return List of projects with the specified status
     */
    List<Project> findByStatus(Status status);

    /**
     * Finds all projects with their associated users loaded
     *
     * @return List of all projects with users
     */
    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.users")
    List<Project> findAllWithUsers();

    /**
     * Finds all projects assigned to a specific user
     *
     * @param userId User ID
     * @return List of projects assigned to the user
     */
    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.users u WHERE u.id = :userId")
    List<Project> findByUserId(@Param("userId") Integer userId);

    /**
     * Finds projects with filtering options
     *
     * @param name Project name filter (partial match, optional)
     * @param manager Project manager registration number filter (optional)
     * @param status Project status filter (optional)
     * @return List of filtered projects
     */
    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.users LEFT JOIN FETCH p.projectManager m " +
            "WHERE (:name IS NULL OR p.name LIKE %:name%) " +
            "AND (:manager IS NULL OR m.matricule = :manager) " +
            "AND (:status IS NULL OR p.status = :status)")
    List<Project> findWithFilters(@Param("name") String name,
                                  @Param("manager") String manager,
                                  @Param("status") Status status);
}