package fr.springboot.backend.controller;

import fr.springboot.backend.dto.RegisterRequest;
import fr.springboot.backend.dto.UserDTO;
import fr.springboot.backend.enums.Role;
import fr.springboot.backend.model.User;
import fr.springboot.backend.repository.UserRepository;
import fr.springboot.backend.service.UserService;
import fr.springboot.backend.util.RolePermissions;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // ========== UTILITAIRE : RÉCUPÉRER L'UTILISATEUR COURANT ==========

    private User getCurrentUser(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("Session invalide");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    // ========== 1. LISTE TOUS LES UTILISATEURS ==========

    @GetMapping
    public ResponseEntity<?> getAllUsers(HttpSession session) {
        try {
            User currentUser = getCurrentUser(session);
            RolePermissions permissions = new RolePermissions(currentUser);

            if (!permissions.canAccessUserList()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", permissions.getAccessDeniedMessage("voir la liste des utilisateurs")));
            }

            List<UserDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(users);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur", "message", e.getMessage()));
        }
    }

    // ========== 2. DÉTAIL D'UN UTILISATEUR ==========

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id, HttpSession session) {
        try {
            User currentUser = getCurrentUser(session);
            User targetUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            RolePermissions permissions = new RolePermissions(currentUser, targetUser);

            if (!permissions.canViewAllUsers() && !permissions.isSelfEdit()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", permissions.getAccessDeniedMessage("voir cet utilisateur")));
            }

            UserDTO user = userService.getUserById(id);
            return ResponseEntity.ok(user);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur", "message", e.getMessage()));
        }
    }



    // ========== 4. CRÉER UN UTILISATEUR ==========

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody RegisterRequest request, HttpSession session) {
        try {
            User currentUser = getCurrentUser(session);
            RolePermissions permissions = new RolePermissions(currentUser);

            if (!permissions.canCreateUser()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", permissions.getAccessDeniedMessage("créer un utilisateur")));
            }

            // Validation du rôle
            String roleError = permissions.validateRoleAssignment(request.getRole(), request.getDepartmentId());
            if (roleError != null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", roleError));
            }

            UserDTO createdUser = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur", "message", e.getMessage()));
        }
    }

    // ========== 5. MODIFIER UN UTILISATEUR (POST AU LIEU DE PUT) ==========

    @PostMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id,
                                        @Valid @RequestBody RegisterRequest request,
                                        HttpSession session) {
        try {
            User currentUser = getCurrentUser(session);
            User targetUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            RolePermissions permissions = new RolePermissions(currentUser, targetUser);

            // Vérifier les permissions selon ce qui est modifié
            boolean needsPrivateInfoPerm = request.getMatricule() != null ||
                    request.getEmail() != null ||
                    request.getRole() != null;

            boolean needsSalaryPerm = request.getBaseSalary() != null;

            if (needsPrivateInfoPerm && !permissions.canUpdatePrivateInfo()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", permissions.getAccessDeniedMessage("modifier les informations privées")));
            }

            if (needsSalaryPerm && !permissions.canUpdateSalary()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", permissions.getAccessDeniedMessage("modifier le salaire")));
            }

            if (!permissions.canUpdatePublicInfo() && !permissions.canUpdatePrivateInfo()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", permissions.getAccessDeniedMessage("modifier cet utilisateur")));
            }

            UserDTO updatedUser = userService.updateUser(id, request);
            return ResponseEntity.ok(updatedUser);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur", "message", e.getMessage()));
        }
    }

    // ========== 6. SUPPRIMER UN UTILISATEUR ==========

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id, HttpSession session) {
        try {
            User currentUser = getCurrentUser(session);
            User targetUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            RolePermissions permissions = new RolePermissions(currentUser, targetUser);

            if (!permissions.canDeleteUserWithTarget()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", permissions.getAccessDeniedMessage("supprimer cet utilisateur")));
            }

            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès"));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur", "message", e.getMessage()));
        }
    }

    // ========== 7. UPLOAD IMAGE ==========

    @PostMapping("/{id}/image")
    public ResponseEntity<?> uploadImage(@PathVariable Integer id,
                                         @RequestParam("image") MultipartFile file,
                                         HttpSession session) {
        try {
            User currentUser = getCurrentUser(session);
            User targetUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            RolePermissions permissions = new RolePermissions(currentUser, targetUser);

            if (!permissions.canUpdatePublicInfo() && !permissions.isSelfEdit()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", permissions.getAccessDeniedMessage("modifier l'image")));
            }

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Aucun fichier fourni"));
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le fichier est trop volumineux (max 5MB)"));
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le fichier doit être une image"));
            }

            targetUser.setProfileImage(file.getBytes());
            userRepository.save(targetUser);

            return ResponseEntity.ok(Map.of("message", "Image uploadée avec succès"));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la lecture du fichier"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur", "message", e.getMessage()));
        }
    }

    // ========== 8. RÉCUPÉRER IMAGE ==========

    @GetMapping("/{id}/image")
    public ResponseEntity<?> getImage(@PathVariable Integer id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            if (user.getProfileImage() == null || user.getProfileImage().length == 0) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(user.getProfileImage());

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== 9. RECHERCHE MULTICRITÈRE ==========

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) Integer positionId,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String searchText,
            HttpSession session) {
        try {
            User currentUser = getCurrentUser(session);
            RolePermissions permissions = new RolePermissions(currentUser);

            if (!permissions.canAccessUserList()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", permissions.getAccessDeniedMessage("rechercher des utilisateurs")));
            }

            List<UserDTO> users = userService.searchUsers(departmentId, positionId, role, grade, searchText);
            return ResponseEntity.ok(users);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur", "message", e.getMessage()));
        }
    }
}