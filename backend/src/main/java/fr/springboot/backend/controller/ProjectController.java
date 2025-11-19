package fr.springboot.backend.controller;

import fr.springboot.backend.dto.ProjectDTO;
import fr.springboot.backend.model.Project;
import fr.springboot.backend.model.User;
import fr.springboot.backend.service.ProjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectDTO> getAll() {
        return projectService.getAllProjectsDTO();
    }

    @GetMapping("/{id}")
    public Project getById(@PathVariable Integer id) {
        return projectService.findById(id).orElse(null);
    }

    @PostMapping
    public Project create(@RequestBody Project project) {
        return projectService.save(project);
    }

    @PutMapping("/{id}/manager")
    public boolean updateManager(@PathVariable Integer id, @RequestBody User manager) {
        return projectService.updateProjectManager(id, manager);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        projectService.delete(id);
    }
}
