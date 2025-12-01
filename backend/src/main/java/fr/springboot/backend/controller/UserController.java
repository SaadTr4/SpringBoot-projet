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

/**
 * REST controller for managing User operations.
 * Provides endpoints for CRUD operations, user management, and profile image handling.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // ========== UTILITY: GET CURRENT USER ==========

    /**
     * Retrieves the current authenticated user from session.
     *
     * @param session the HTTP session
     * @return the current user
     * @throws RuntimeException if session is invalid or user not found
     */
    private User getCurrentUser(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("Session invalide");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    // ========== 1. GET ALL USERS ==========

    /**
     * Retrieves all users with permission checks.
     *
     * @param session the HTTP session
     * @return ResponseEntity with list of users or error
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers(HttpSession session) {
        try {
            User currentUser = getCurrentUser(session);
            RolePermissions permissions = new RolePermissions(currentUser);

            if (!permissions.canAccessUserList()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", permissions.getAccessDeniedMessage("view user list")));
            }

            List<UserDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(users);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur", "message", e.getMessage()));
        }
    }

    // ========== 2. GET USER BY ID ==========

    /**
     * Retrieves a specific user by ID with permission checks.
     *
     * @param id the user ID
     * @param session the HTTP session
     * @return ResponseEntity with user or error
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id, HttpSession session) {
        try {
            User currentUser = getCurrentUser(session);
            User targetUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            RolePermissions permissions = new RolePermissions(currentUser, targetUser);

            if (!permissions.canViewAllUsers() && !permissions.isSelfEdit()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", permissions.getAccessDeniedMessage("view this user")));
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

    // ========== 3. CREATE USER ==========

    /**
     * Creates a new user with permission and role validation.
     *
     * @param request the user registration request
     * @param session the HTTP session
     * @return ResponseEntity with created user or error
     */
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody RegisterRequest request, HttpSession session) {
        try {
            User currentUser = getCurrentUser(session);
            RolePermissions permissions = new RolePermissions(currentUser);

            if (!permissions.canCreateUser()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", permissions.getAccessDeniedMessage("create a user")));
            }

            // Role validation
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

    // ========== 4. UPDATE USER (POST INSTEAD OF PUT) ==========

    /**
     * Updates an existing user with comprehensive permission checks.
     *
     * @param id the user ID to update
     * @param request the updated user data
     * @param session the HTTP session
     * @return ResponseEntity with updated user or error
     */
    @PostMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id,
                                        @Valid @RequestBody RegisterRequest request,
                                        HttpSession session) {
        try {
            User currentUser = getCurrentUser(session);
            User targetUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            RolePermissions permissions = new RolePermissions(currentUser, targetUser);

            // Check permissions based on what is being modified
            boolean needsPrivateInfoPerm = request.getMatricule() != null ||
                    request.getEmail() != null ||
                    request.getRole() != null;

            boolean needsSalaryPerm = request.getBaseSalary() != null;

            if (needsPrivateInfoPerm && !permissions.canUpdatePrivateInfo()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", permissions.getAccessDeniedMessage("modify private information")));
            }

            if (needsSalaryPerm && !permissions.canUpdateSalary()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", permissions.getAccessDeniedMessage("modify salary")));
            }

            if (!permissions.canUpdatePublicInfo() && !permissions.canUpdatePrivateInfo()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", permissions.getAccessDeniedMessage("modify this user")));
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

    // ========== 5. DELETE USER ==========

    /**
     * Deletes a user with permission checks.
     *
     * @param id the user ID to delete
     * @param session the HTTP session
     * @return ResponseEntity with success message or error
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id, HttpSession session) {
        try {
            User currentUser = getCurrentUser(session);
            User targetUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            RolePermissions permissions = new RolePermissions(currentUser, targetUser);

            if (!permissions.canDeleteUserWithTarget()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", permissions.getAccessDeniedMessage("delete this user")));
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

    // ========== 6. UPLOAD PROFILE IMAGE ==========

    /**
     * Uploads a profile image for a user.
     *
     * @param id the user ID
     * @param file the image file
     * @param session the HTTP session
     * @return ResponseEntity with success message or error
     */
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
                        .body(Map.of("error", permissions.getAccessDeniedMessage("modify image")));
            }

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Aucun fichier fourni"));
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of("error", "Fichier trop volumineux (max 5MB)"));
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le fichier doit être une image"));
            }

            targetUser.setProfileImage(file.getBytes());
            userRepository.save(targetUser);

            return ResponseEntity.ok(Map.of("message", "Image téléchargée avec succès"));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur de lecture du fichier"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur", "message", e.getMessage()));
        }
    }

    // ========== 7. GET PROFILE IMAGE ==========

    /**
     * Retrieves the profile image for a user.
     *
     * @param id the user ID
     * @return ResponseEntity with image data or 404 if not found
     */
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

    // ========== 8. SEARCH USERS ==========

    /**
     * Searches users with multiple criteria and permission checks.
     *
     * @param departmentId the department ID filter (optional)
     * @param positionId the position ID filter (optional)
     * @param role the role filter (optional)
     * @param grade the grade filter (optional)
     * @param searchText the text search filter (optional)
     * @param session the HTTP session
     * @return ResponseEntity with filtered users or error
     */
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
                        .body(Map.of("error", permissions.getAccessDeniedMessage("search users")));
            }

            List<UserDTO> users = userService.searchUsers(departmentId, positionId, role, grade, searchText);
            return ResponseEntity.ok(users);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur serveur", "message", e.getMessage()));
        }
    }
}