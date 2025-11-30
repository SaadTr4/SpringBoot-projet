package fr.springboot.backend.repository;

import fr.springboot.backend.model.Position;
import fr.springboot.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Integer> {

    Optional<Position> findByNameIgnoreCase(String name);

    @Query("SELECT u FROM User u WHERE u.position.id = :positionId")
    List<User> findUsersByPosition(@Param("positionId") Integer positionId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.position.id = :positionId")
    long countUsersByPosition(@Param("positionId") Integer positionId);

    @Query("SELECT u FROM User u WHERE u.matricule = :matricule")
    Optional<User> findUserByMatricule(@Param("matricule") String matricule);
}
