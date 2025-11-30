package fr.springboot.backend.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity class representing a department in the organization.
 * Maps to the "department" table in the database.
 */
@Entity
@Table(name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Department name
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Unique department code
     */
    @Column(nullable = false, unique = true, length = 20)
    private String code;

    /**
     * Department description
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Users belonging to this department
     */
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    // Constructors

    /**
     * Default constructor
     */
    public Department() {}

    /**
     * Constructor with parameters
     *
     * @param name Department name
     * @param code Department code
     * @param description Department description
     */
    public Department(String name, String code, String description) {
        this.name = name;
        this.code = code;
        this.description = description;
    }

    // Getters and Setters

    /**
     * Gets the department ID
     *
     * @return Department ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the department ID
     *
     * @param id Department ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the department name
     *
     * @return Department name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the department name
     *
     * @param name Department name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the department code
     *
     * @return Department code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the department code
     *
     * @param code Department code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the department description
     *
     * @return Department description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the department description
     *
     * @param description Department description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
