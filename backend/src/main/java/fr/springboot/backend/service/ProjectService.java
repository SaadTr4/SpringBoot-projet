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

/**
 * Service class for managing project operations.
 * Provides business logic for project management, user assignments, and status updates.
 */
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    // ==================== CRUD ====================

    /**
     * Saves a project (create or update)
     *
     * @param project Project to save
     * @return Saved project
     */
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    /**
     * Finds a project by its ID
     *
     * @param id Project ID
     * @return Optional containing the project if found
     */
    public Optional<Project> findById(Integer id) {
        return projectRepository.findById(id);
    }

    /**
     * Retrieves all projects with their users loaded
     *
     * @return List of all projects with users
     */
    public List<Project> findAll() {
        return projectRepository.findAllWithUsers();
    }

    /**
     * Retrieves all projects as DTOs
     *
     * @return List of all projects as ProjectDTO
     */
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

    /**
     * Deletes a project by ID
     *
     * @param id Project ID to delete
     */
    public void delete(Integer id) {
        projectRepository.deleteById(id);
    }

    // ==================== STATUS ====================

    /**
     * Updates the status of a project
     *
     * @param id Project ID
     * @param status New status
     * @return true if update successful, false otherwise
     */
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

    /**
     * Assigns a user to a project
     *
     * @param projectId Project ID
     * @param userId User ID
     * @return true if assignment successful, false otherwise
     */
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

    /**
     * Removes a user from a project
     *
     * @param projectId Project ID
     * @param userId User ID
     * @return true if removal successful, false otherwise
     */
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

    /**
     * Updates the project manager for a project
     *
     * @param projectId Project ID
     * @param managerId New manager user ID
     * @return true if update successful, false otherwise
     */
    @Transactional
    public boolean updateProjectManager(Integer projectId, Integer managerId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        Optional<User> managerOpt = userRepository.findById(managerId);

        if (projectOpt.isPresent() && managerOpt.isPresent()) {
            Project project = projectOpt.get();
            User newManager = managerOpt.get();

            // Remove old manager
            User oldManager = project.getProjectManager();

            if (oldManager != null && !oldManager.getId().equals(newManager.getId())) {
                project.getUsers().remove(oldManager);
                oldManager.getProjects().remove(project);
            }

            // Add new manager to project
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

    /**
     * Finds projects with filtering options
     *
     * @param name Project name filter (partial match, optional)
     * @param managerMatricule Project manager registration number filter (optional)
     * @param status Project status filter (optional)
     * @return List of filtered projects
     */
    public List<Project> findWithFilters(String name, String managerMatricule, Status status) {
        return projectRepository.findWithFilters(name, managerMatricule, status);
    }
}