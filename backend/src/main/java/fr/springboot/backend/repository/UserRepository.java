package fr.springboot.backend.repository;

import fr.springboot.backend.enums.Role;
import fr.springboot.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Trouver un utilisateur par matricule
    Optional<User> findByMatricule(String matricule);

    // Trouver un utilisateur par email
    Optional<User> findByEmail(String email);

    //  Trouver tous les utilisateurs par rôle
    List<User> findByRole(Role role);

    // Récupérer la prochaine valeur de la séquence pour générer un matricule
    @Query(value = "SELECT nextval('emp_seq')", nativeQuery = true)
    Long getNextMatriculeSequence();
}