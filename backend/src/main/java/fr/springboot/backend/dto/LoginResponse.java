package fr.springboot.backend.dto;

/**
 * DTO for login response containing user authentication details.
 * Returned to frontend after successful login.
 */
public class LoginResponse {
    private String token;
    private String type;
    private String matricule;
    private String fullName;
    private String email;
    private String role;
    private String department;

    /**
     * Default constructor (required)
     */
    public LoginResponse() {
    }

    /**
     * Constructor with all parameters
     *
     * @param token the session token
     * @param matricule the registration number
     * @param fullName the user's full name
     * @param email the user's email
     * @param role the user's role
     * @param department the user's department
     */
    public LoginResponse(String token, String matricule, String fullName, String email, String role, String department) {
        this.token = token;
        this.type = "Session";  // Default value
        this.matricule = matricule;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.department = department;
    }

    // GETTERS AND SETTERS

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}