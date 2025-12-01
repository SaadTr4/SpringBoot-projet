package fr.springboot.backend.dto;

import fr.springboot.backend.enums.Status;

/**
 * DTO for project data transfer.
 * Contains simplified project information for frontend display.
 */
public class ProjectDTO {

    private Integer id;
    private String name;
    private String description;
    private Status status;
    private String projectManagerName; // just the manager's name

    // ===========================
    // Constructors
    // ===========================

    /**
     * Default constructor
     */
    public ProjectDTO() {
    }

    /**
     * Parameterized constructor with all fields
     *
     * @param id the project ID
     * @param name the project name
     * @param description the project description
     * @param status the project status
     * @param projectManagerName the project manager's name
     */
    public ProjectDTO(Integer id, String name, String description, Status status, String projectManagerName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.projectManagerName = projectManagerName;
    }

    // ===========================
    // Getters / Setters
    // ===========================

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getProjectManagerName() { return projectManagerName; }
    public void setProjectManagerName(String projectManagerName) { this.projectManagerName = projectManagerName; }
}