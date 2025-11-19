package fr.springboot.backend.service;

import fr.springboot.backend.dto.ProjectDTO;
import fr.springboot.backend.model.Project;
import fr.springboot.backend.model.User;
import fr.springboot.backend.enums.Status;
import fr.springboot.backend.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project save(Project project) {
        // Pour l'instant, on ne gère pas la persistance des Users
        return projectRepository.save(project);
    }

    public List<Project> findAll() {
        return projectRepository.findAllWithUsers();
    }
    public List<ProjectDTO> getAllProjectsDTO() {
        return findAll().stream()
                .map(p -> new ProjectDTO(
                        p.getId(),
                        p.getName(),
                        p.getDescription(),
                        p.getStatus(),
                        p.getProjectManager() != null ? p.getProjectManager().getFullName() : null
                ))
                .collect(Collectors.toList());
    }
    public Optional<Project> findById(Integer id) {
        return projectRepository.findById(id);
    }

    public List<Project> findByUserId(Integer userId) {
        return projectRepository.findByUserId(userId);
    }

    public List<Project> findWithFilters(String name, String managerMatricule, Status status) {
        return projectRepository.findWithFilters(name, managerMatricule, status);
    }

    @Transactional
    public boolean assignUserToProject(Integer projectId, User user) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isPresent() && user != null) {
            Project project = projectOpt.get();
            project.getUsers().add(user); // juste en mémoire, pas de save sur user
            return true;
        }
        return false;
    }

    @Transactional
    public boolean updateProjectManager(Integer projectId, User manager) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isPresent() && manager != null) {
            Project project = projectOpt.get();
            project.setProjectManager(manager); // juste en mémoire pour l'instant
            projectRepository.save(project);
            return true;
        }
        return false;
    }

    public void delete(Integer id) {
        projectRepository.deleteById(id);
    }
}