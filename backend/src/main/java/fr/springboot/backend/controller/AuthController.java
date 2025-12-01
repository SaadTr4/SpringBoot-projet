package fr.springboot.backend.controller;

import fr.springboot.backend.dto.LoginRequest;
import fr.springboot.backend.dto.LoginResponse;
import fr.springboot.backend.model.User;
import fr.springboot.backend.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication controller for handling user login, logout and session management
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Handles user login authentication
     *
     * @param request Login request containing credentials
     * @param session HTTP session
     * @return ResponseEntity with login response or error
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        try {
            System.out.println(" Tentative de login pour: " + request.getMatricule());

            // Find user by matricule
            User user = userRepository.findByMatricule(request.getMatricule())
                    .orElseThrow(() -> new RuntimeException("Matricule ou mot de passe incorrect"));

            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                System.out.println(" Mot de passe incorrect");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Matricule ou mot de passe incorrect"));
            }

            // Authenticate in Spring Security
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.getMatricule(),
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );

            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);

            // Save in session
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
            session.setAttribute("userId", user.getId());
            session.setAttribute("matricule", user.getMatricule());
            session.setAttribute("role", user.getRole().name());
            session.setAttribute("fullName", user.getFullName());

            System.out.println(" Session créée et authentification Spring Security OK: " + session.getId());
            System.out.println("   - User ID: " + user.getId());
            System.out.println("   - Matricule: " + user.getMatricule());
            System.out.println("   - Rôle: " + user.getRole());

            // Create response
            LoginResponse response = new LoginResponse();
            response.setToken(session.getId());
            response.setType("Session");
            response.setMatricule(user.getMatricule());
            response.setFullName(user.getFullName());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole().name());
            response.setDepartment(user.getDepartment() != null ? user.getDepartment().getName() : null);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println(" Erreur login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Handles user logout by clearing security context and invalidating session
     *
     * @param session HTTP session to invalidate
     * @return ResponseEntity with logout confirmation message
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        System.out.println(" Logout - Session ID: " + session.getId());
        SecurityContextHolder.clearContext();
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Déconnexion réussie"));
    }

    /**
     * Checks if the current session is valid and authenticated
     *
     * @param session HTTP session to check
     * @return ResponseEntity with session information or unauthorized status
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkSession(HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("authenticated", false));
        }
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("authenticated", false));
        }

        User user = userRepository.findById(userId)
                .orElse(null);

        Map<String, Object> info = new HashMap<>();
        info.put("authenticated", true);
        info.put("userId", session.getAttribute("userId"));
        info.put("matricule", session.getAttribute("matricule"));
        info.put("role", session.getAttribute("role"));
        info.put("sessionId", session.getId());

        if (user != null && user.getDepartment() != null) {
            info.put("department", user.getDepartment().getName());
            info.put("departmentId", user.getDepartment().getId());
        } else {
            info.put("department", null);
            info.put("departmentId", null);
        }
        return ResponseEntity.ok(info);
    }
}