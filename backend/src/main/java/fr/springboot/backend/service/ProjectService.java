package fr.springboot.backend.service;

import fr.springboot.backend.dto.ProjectDTO;
import fr.springboot.backend.enums.Status;
import fr.springboot.backend.model.Project;
import fr.springboot.backend.model.User;
import fr.springboot.backend.repository.ProjectRepository;
import fr.springboot.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    // ==================== CRUD ====================

    public Project save(Project project) {
        return projectRepository.save(project);
    }

    public Optional<Project> findById(Integer id) {
        return projectRepository.findById(id);
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

    public void delete(Integer id) {
        projectRepository.deleteById(id);
    }

    // ==================== STATUS ====================

    @Transactional
    public boolean updateStatus(Integer id, Status status) {
        return projectRepository.findById(id)
                .map(project -> {
                    project.setStatus(status);
                    projectRepository.save(project);
                    return true;
                })
                .orElse(false);
    }

    // ==================== USER-PROJECT RELATIONS ====================

    @Transactional
    public boolean assignUserToProject(Integer projectId, Integer userId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (projectOpt.isPresent() && userOpt.isPresent()) {
            Project project = projectOpt.get();
            User user = userOpt.get();

            project.getUsers().add(user);
            user.getProjects().add(project);

            projectRepository.save(project);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean removeUserFromProject(Integer projectId, Integer userId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (projectOpt.isPresent() && userOpt.isPresent()) {
            Project project = projectOpt.get();
            User user = userOpt.get();

            project.getUsers().remove(user);
            user.getProjects().remove(project);

            projectRepository.save(project);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean updateProjectManager(Integer projectId, Integer managerId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        Optional<User> managerOpt = userRepository.findById(managerId);

        if (projectOpt.isPresent() && managerOpt.isPresent()) {
            Project project = projectOpt.get();
            User newManager = managerOpt.get();

            // ancien manager
            User oldManager = project.getProjectManager();

            if (oldManager != null && !oldManager.getId().equals(newManager.getId())) {
                project.getUsers().remove(oldManager);
                oldManager.getProjects().remove(project);
            }

            // ajoute le nouveau manager au projet
            if (!project.getUsers().contains(newManager)) {
                project.getUsers().add(newManager);
                newManager.getProjects().add(project);
            }

            project.setProjectManager(newManager);
            projectRepository.save(project);
            return true;
        }
        return false;
    }

    public List<Project> findWithFilters(String name, String managerMatricule, Status status) {
        return projectRepository.findWithFilters(name, managerMatricule, status);
    }
}
