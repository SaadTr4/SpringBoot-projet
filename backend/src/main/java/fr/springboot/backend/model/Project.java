package fr.springboot.backend.model;

import fr.springboot.backend.enums.Status;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class representing a project in the organization.
 * Maps to the "project" table in the database.
 */
@Entity
@Table(name = "project")
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * Project name
     */
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    /**
     * Project description
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Project status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private Status status;

    /**
     * Project manager responsible for the project
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_manager_id")
    private User projectManager;

    /**
     * Users assigned to this project
     */
    @ManyToMany(mappedBy = "projects", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    // ==============================
    // Constructors
    // ==============================

    /**
     * Default constructor
     */
    public Project() {
        this.status = Status.IN_PROGRESS; // Default status
    }

    /**
     * Constructor with name and description
     *
     * @param name Project name
     * @param description Project description
     */
    public Project(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.IN_PROGRESS; // Default status
    }

    /**
     * Constructor with name, project manager and status
     *
     * @param name Project name
     * @param projectManager Project manager
     * @param status Project status
     */
    public Project(String name, User projectManager, Status status) {
        this.name = name;
        this.projectManager = projectManager;
        this.status = status;
    }

    /**
     * Constructor with all parameters
     *
     * @param name Project name
     * @param projectManager Project manager
     * @param description Project description
     * @param status Project status
     */
    public Project(String name, User projectManager, String description, Status status) {
        this.name = name;
        this.projectManager = projectManager;
        this.description = description;
        this.status = status;
    }

    // ========================================
    // GETTERS / SETTERS
    // ========================================

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public User getProjectManager() { return projectManager; }
    public void setProjectManager(User projectManager) { this.projectManager = projectManager; }
    public Set<User> getUsers() { return users; }
    public void setUsers(Set<User> users) { this.users = users; }

    // ==============================
    // Utility Methods
    // ==============================

    @Override
    public String toString() {
        return "Project [\n" +
                "               id  = " + id + ",\n" +
                "             name  = " + name + ",\n" +
                "           status  = " + status + ",\n" +
                "   projectManager  = " + projectManager.getFullName() + "\n" +
                "      description  = " + description + "\n" +
                ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        Project other = (Project) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
