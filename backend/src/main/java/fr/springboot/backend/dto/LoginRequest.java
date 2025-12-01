package fr.springboot.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for login request containing user credentials.
 * Used when frontend sends login data to backend.
 *
 * EXAMPLE REQUEST:
 * POST /api/auth/login
 * {
 *   "matricule": "EMP1A42B",
 *   "password": "password123"
 * }
 */
@Data
public class LoginRequest {

    /**
     * Employee registration number
     *
     * @NotBlank : Cannot be empty or null
     */
    @NotBlank(message = "Le matricule est obligatoire")
    private String matricule;

    /**
     * Password (will be compared with hash in database)
     */
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;
}