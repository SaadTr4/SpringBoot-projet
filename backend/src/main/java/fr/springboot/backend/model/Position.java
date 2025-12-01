package fr.springboot.backend.model;

import jakarta.persistence.*;

/**
 * Entity class representing a job position in the organization.
 * Maps to the "position" table in the database.
 */
@Entity
@Table(name = "position")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Position name
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Position description
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    // Constructors

    /**
     * Default constructor
     */
    public Position() {}

    /**
     * Constructor with parameters
     *
     * @param name Position name
     * @param description Position description
     */
    public Position(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters

    /**
     * Gets the position ID
     *
     * @return Position ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the position ID
     *
     * @param id Position ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the position name
     *
     * @return Position name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the position name
     *
     * @param name Position name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the position description
     *
     * @return Position description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the position description
     *
     * @param description Position description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
