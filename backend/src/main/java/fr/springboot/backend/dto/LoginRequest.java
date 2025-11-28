package fr.springboot.backend.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 *  DTO LOGIN REQUEST : Données de connexion
 *
 *  UTILISATION : Frontend envoie ce JSON au backend
 *
 * EXEMPLE DE REQUÊTE :
 * POST /api/auth/login
 * {
 *   "matricule": "EMP1A42B",
 *   "password": "motdepasse123"
 * }
 */
@Data
public class LoginRequest {

    /**
     *  Matricule de l'employé
     *
     * @NotBlank : Ne peut pas être vide ou null
     */
    @NotBlank(message = "Le matricule est obligatoire")
    private String matricule;

    /**
     * 🔒 Mot de passe (sera comparé avec le hash en BDD)
     */
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;
}
