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

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    private UserRepository userRepository;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // ==================== LIST & GET ====================

    @GetMapping
    public List<ProjectDTO> getAll() {
        return projectService.getAllProjectsDTO();
    }

    @GetMapping("/{id}")
    public Project getById(@PathVariable Integer id) {
        return projectService.findById(id).orElse(null);
    }

    // ==================== CREATE ====================

    @PostMapping
    public Project create(@RequestBody Project project) {

        // Sécurise le manager si envoyé avec seulement un id
        if (project.getProjectManager() != null && project.getProjectManager().getId() != null) {
            User manager = userRepository.findById(project.getProjectManager().getId()).orElse(null);
            project.setProjectManager(manager);

            if (manager != null) {
                project.getUsers().add(manager); // ajoute automatiquement le manager à la liste des users
            }
        }

        return projectService.save(project);
    }

    // ==================== UPDATE ====================

    @PutMapping("/{id}/manager/{managerId}")
    public boolean updateManager(@PathVariable Integer id, @PathVariable Integer managerId) {
        return projectService.updateProjectManager(id, managerId);
    }

    @PutMapping("/{id}/assign/{userId}")
    public boolean assignUser(@PathVariable Integer id, @PathVariable Integer userId) {
        return projectService.assignUserToProject(id, userId);
    }

    @PutMapping("/{id}/remove/{userId}")
    public boolean removeUser(@PathVariable Integer id, @PathVariable Integer userId) {
        return projectService.removeUserFromProject(id, userId);
    }

    @PutMapping("/{id}/status")
    public boolean updateStatus(@PathVariable Integer id, @RequestParam Status status) {
        return projectService.updateStatus(id, status);
    }

    // ==================== DELETE ====================

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        projectService.delete(id);
    }

    // ==================== FILTER ====================

    @GetMapping("/filter")
    public List<Project> filterProjects(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String managerMatricule,
            @RequestParam(required = false) Status status
    ) {
        return projectService.findWithFilters(name, managerMatricule, status);
    }
}
