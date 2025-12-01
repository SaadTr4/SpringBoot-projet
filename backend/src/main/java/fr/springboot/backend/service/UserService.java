package fr.springboot.backend.service;

import fr.springboot.backend.dto.RegisterRequest;
import fr.springboot.backend.dto.UserDTO;
import fr.springboot.backend.enums.Grade;
import fr.springboot.backend.enums.Role;
import fr.springboot.backend.model.Department;
import fr.springboot.backend.model.Position;
import fr.springboot.backend.model.User;
import fr.springboot.backend.repository.DepartmentRepository;
import fr.springboot.backend.repository.PositionRepository;
import fr.springboot.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.math.BigDecimal;

/**
 * Service class for managing user operations.
 * Provides business logic for user management, registration, and search functionality.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       DepartmentRepository departmentRepository,
                       PositionRepository positionRepository,
                       @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Generates a unique registration number in format: EMP2F37Q
     *
     * @return Generated registration number
     */
    public String generateMatricule() {
        try {
            // 1. Get PostgreSQL sequence value
            Long seq = userRepository.getNextMatriculeSequence();

            // 2. Generate two random letters
            Random random = new Random();
            char letter1 = (char) ('A' + random.nextInt(26));
            char letter2 = (char) ('A' + random.nextInt(26));

            // 3. Generate random number between 1 and 99
            int number = random.nextInt(99) + 1;

            // 4. Assemble: EMP{seq}{letter1}{number}{letter2}
            String matricule = String.format("EMP%d%c%02d%c", seq, letter1, number, letter2);

            System.out.println(" Matricule généré : " + matricule);
            return matricule;

        } catch (Exception e) {
            System.err.println("Erreur génération matricule : " + e.getMessage());
            e.printStackTrace();
            // Fallback
            return "EMP" + System.currentTimeMillis();
        }
    }

    /**
     * Converts User entity to UserDTO
     *
     * @param user User entity
     * @return UserDTO object
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setMatricule(user.getMatricule());
        dto.setLastName(user.getLastName());
        dto.setFirstName(user.getFirstName());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());

        if (user.getGrade() != null) {
            dto.setGrade(user.getGrade().getDisplayName());
        }

        dto.setRole(user.getRole().name());

        if (user.getContractType() != null) {
            dto.setContractType(user.getContractType().getDisplayName());
        }

        dto.setBaseSalary(user.getBaseSalary() != null ? user.getBaseSalary().doubleValue() : null);

        if (user.getDepartment() != null) {
            dto.setDepartment(user.getDepartment().getName());
            dto.setDepartmentId(user.getDepartment().getId());
        }

        if (user.getPosition() != null) {
            dto.setPosition(user.getPosition().getName());
            dto.setPositionId(user.getPosition().getId());
        }

        return dto;
    }

    /**
     * Retrieves all users as DTOs
     *
     * @return List of all users as UserDTO
     */
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user by ID as DTO
     *
     * @param id User ID
     * @return UserDTO object
     * @throws RuntimeException if user not found
     */
    public UserDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));
        return convertToDTO(user);
    }

    /**
     * Creates a new user with validation and automatic registration number generation
     *
     * @param request User registration request
     * @return Created user as DTO
     * @throws RuntimeException if validation fails or resources not found
     */
    public UserDTO createUser(RegisterRequest request) {

        // Manual validation for required fields
        if (request.getLastName() == null || request.getLastName().isBlank()) {
            throw new RuntimeException("Le nom est obligatoire");
        }
        if (request.getFirstName() == null || request.getFirstName().isBlank()) {
            throw new RuntimeException("Le prénom est obligatoire");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("L'email est obligatoire");
        }
        if (request.getRole() == null) {
            throw new RuntimeException("Le rôle est obligatoire");
        }
        if (request.getGrade() == null) {
            throw new RuntimeException("Le grade est obligatoire");
        }
        if (request.getContractType() == null) {
            throw new RuntimeException("Le type de contrat est obligatoire");
        }

        // Generate automatic registration number
        String matricule = generateMatricule();

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("L'email existe déjà");
        }

        // Create new user
        User user = new User();
        user.setMatricule(matricule);
        user.setLastName(request.getLastName());
        user.setFirstName(request.getFirstName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setGrade(request.getGrade());
        user.setRole(request.getRole());
        user.setContractType(request.getContractType());

        if (request.getBaseSalary() != null) {
            user.setBaseSalary(BigDecimal.valueOf(request.getBaseSalary()));
        }

        // Default password
        user.setPassword(passwordEncoder.encode("motdepasse123"));

        // Assign department
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Département non trouvé"));
            user.setDepartment(department);
        }

        // Assign position
        if (request.getPositionId() != null) {
            Position position = positionRepository.findById(request.getPositionId())
                    .orElseThrow(() -> new RuntimeException("Poste non trouvé"));
            user.setPosition(position);
        }

        // Save user
        User savedUser = userRepository.save(user);

        System.out.println(" Utilisateur créé avec matricule auto-généré : " + matricule);

        return convertToDTO(savedUser);
    }

    /**
     * Updates an existing user
     *
     * @param id User ID to update
     * @param request Updated user data
     * @return Updated user as DTO
     * @throws RuntimeException if user not found or validation fails
     */
    public UserDTO updateUser(Integer id, RegisterRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (request.getMatricule() != null && !request.getMatricule().equals(user.getMatricule())) {
            if (userRepository.findByMatricule(request.getMatricule()).isPresent()) {
                throw new RuntimeException("Le matricule existe déjà");
            }
            user.setMatricule(request.getMatricule());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new RuntimeException("L'email existe déjà");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getGrade() != null) user.setGrade(request.getGrade());
        if (request.getRole() != null) user.setRole(request.getRole());
        if (request.getContractType() != null) user.setContractType(request.getContractType());

        if (request.getBaseSalary() != null) {
            user.setBaseSalary(BigDecimal.valueOf(request.getBaseSalary()));
        }

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Département non trouvé"));
            user.setDepartment(department);
        }

        if (request.getPositionId() != null) {
            Position position = positionRepository.findById(request.getPositionId())
                    .orElseThrow(() -> new RuntimeException("Poste non trouvé"));
            user.setPosition(position);
        }

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    /**
     * Deletes a user by ID
     *
     * @param id User ID to delete
     * @throws RuntimeException if user not found
     */
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        userRepository.deleteById(id);
    }

    /**
     * Searches users with multiple criteria
     *
     * @param departmentId Department ID filter (optional)
     * @param positionId Position ID filter (optional)
     * @param roleStr Role filter (optional)
     * @param gradeStr Grade filter (optional)
     * @param searchText Text search filter (optional)
     * @return List of filtered users as DTO
     */
    public List<UserDTO> searchUsers(Integer departmentId, Integer positionId,
                                     String roleStr, String gradeStr, String searchText) {
        List<User> users = userRepository.findAll();

        if (departmentId != null) {
            users = users.stream()
                    .filter(u -> u.getDepartment() != null && u.getDepartment().getId().equals(departmentId))
                    .collect(Collectors.toList());
        }

        if (positionId != null) {
            users = users.stream()
                    .filter(u -> u.getPosition() != null && u.getPosition().getId().equals(positionId))
                    .collect(Collectors.toList());
        }

        if (roleStr != null && !roleStr.isBlank()) {
            try {
                Role role = Role.valueOf(roleStr);
                users = users.stream()
                        .filter(u -> u.getRole() == role)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException ignored) {}
        }

        if (gradeStr != null && !gradeStr.isBlank()) {
            try {
                Grade grade = Grade.valueOf(gradeStr);
                users = users.stream()
                        .filter(u -> u.getGrade() == grade)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException ignored) {}
        }

        if (searchText != null && !searchText.isBlank()) {
            String search = searchText.toLowerCase();
            users = users.stream()
                    .filter(u ->
                            u.getFirstName().toLowerCase().contains(search) ||
                                    u.getLastName().toLowerCase().contains(search) ||
                                    u.getMatricule().toLowerCase().contains(search)
                    )
                    .collect(Collectors.toList());
        }

        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}