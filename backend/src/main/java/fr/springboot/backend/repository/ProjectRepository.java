package fr.springboot.backend.repository;

import fr.springboot.backend.model.Project;
import fr.springboot.backend.model.User;
import fr.springboot.backend.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Integer> {

    Optional<Project> findByName(String name);

    List<Project> findByStatus(Status status);

    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.users")
    List<Project> findAllWithUsers();

    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.users u WHERE u.id = :userId")
    List<Project> findByUserId(@Param("userId") Integer userId);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.users LEFT JOIN FETCH p.projectManager m " +
            "WHERE (:name IS NULL OR p.name LIKE %:name%) " +
            "AND (:manager IS NULL OR m.matricule = :manager) " +
            "AND (:status IS NULL OR p.status = :status)")
    List<Project> findWithFilters(@Param("name") String name,
                                  @Param("manager") String manager,
                                  @Param("status") Status status);
}
