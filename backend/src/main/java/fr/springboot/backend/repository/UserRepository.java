package fr.springboot.backend.repository;

import fr.springboot.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByMatricule(String matricule);

    Optional<User> findByEmail(String email);

    //  MÉTHODE POUR GÉNÉRER LE MATRICULE AVEC SÉQUENCE
    @Query(value = "SELECT nextval('emp_seq')", nativeQuery = true)
    Long getNextMatriculeSequence();
}