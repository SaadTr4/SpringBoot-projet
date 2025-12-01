package fr.springboot.backend.controller;

import fr.springboot.backend.dto.ProjectDTO;
import fr.springboot.backend.model.Project;
import fr.springboot.backend.model.User;
import fr.springboot.backend.enums.Status;
import fr.springboot.backend.repository.UserRepository;
import fr.springboot.backend.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Project operations.
 * Provides endpoints for CRUD operations, user assignments, and project management.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Constructor with dependency injection.
     *
     * @param projectService the project service
     */
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // ==================== LIST & GET ENDPOINTS ====================

    /**
     * Retrieves all projects as DTOs.
     *
     * @return list of all project DTOs
     */
    @GetMapping
    public List<ProjectDTO> getAll() {
        return projectService.getAllProjectsDTO();
    }

    /**
     * Retrieves a project by its ID.
     *
     * @param id the project ID
     * @return the project if found, null otherwise
     */
    @GetMapping("/{id}")
    public Project getById(@PathVariable Integer id) {
        return projectService.findById(id).orElse(null);
    }

    // ==================== CREATE ENDPOINTS ====================

    /**
     * Creates a new project.
     *
     * @param project the project to create
     * @return the created project
     */
    @PostMapping
    public Project create(@RequestBody Project project) {

        // Secure the manager if sent with only an ID
        if (project.getProjectManager() != null && project.getProjectManager().getId() != null) {
            User manager = userRepository.findById(project.getProjectManager().getId()).orElse(null);
            project.setProjectManager(manager);

            if (manager != null) {
                project.getUsers().add(manager); // automatically add manager to user list
            }
        }

        return projectService.save(project);
    }

    // ==================== UPDATE ENDPOINTS ====================

    /**
     * Updates the project manager for a project.
     *
     * @param id the project ID
     * @param managerId the new manager user ID
     * @return true if update successful, false otherwise
     */
    @PutMapping("/{id}/manager/{managerId}")
    public boolean updateManager(@PathVariable Integer id, @PathVariable Integer managerId) {
        return projectService.updateProjectManager(id, managerId);
    }

    /**
     * Assigns a user to a project.
     *
     * @param id the project ID
     * @param userId the user ID to assign
     * @return true if assignment successful, false otherwise
     */
    @PutMapping("/{id}/assign/{userId}")
    public boolean assignUser(@PathVariable Integer id, @PathVariable Integer userId) {
        return projectService.assignUserToProject(id, userId);
    }

    /**
     * Removes a user from a project.
     *
     * @param id the project ID
     * @param userId the user ID to remove
     * @return true if removal successful, false otherwise
     */
    @PutMapping("/{id}/remove/{userId}")
    public boolean removeUser(@PathVariable Integer id, @PathVariable Integer userId) {
        return projectService.removeUserFromProject(id, userId);
    }

    /**
     * Updates the status of a project.
     *
     * @param id the project ID
     * @param status the new status
     * @return true if update successful, false otherwise
     */
    @PutMapping("/{id}/status")
    public boolean updateStatus(@PathVariable Integer id, @RequestParam Status status) {
        return projectService.updateStatus(id, status);
    }

    // ==================== DELETE ENDPOINTS ====================

    /**
     * Deletes a project.
     *
     * @param id the project ID to delete
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        projectService.delete(id);
    }

    // ==================== FILTER ENDPOINTS ====================

    /**
     * Filters projects based on criteria.
     *
     * @param name the project name (optional)
     * @param managerMatricule the manager registration number (optional)
     * @param status the project status (optional)
     * @return list of filtered projects
     */
    @GetMapping("/filter")
    public List<Project> filterProjects(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String managerMatricule,
            @RequestParam(required = false) Status status
    ) {
        return projectService.findWithFilters(name, managerMatricule, status);
    }
}