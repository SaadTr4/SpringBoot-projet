package fr.springboot.backend.dto;

import fr.springboot.backend.enums.Status;

public class ProjectDTO {

    private Integer id;
    private String name;
    private String description;
    private Status status;
    private String projectManagerName; // juste le nom du manager

    // ===========================
    // Constructeurs
    // ===========================
    public ProjectDTO() {
    }

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