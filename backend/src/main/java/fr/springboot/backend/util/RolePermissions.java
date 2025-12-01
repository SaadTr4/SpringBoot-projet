package fr.springboot.backend.util;

import fr.springboot.backend.enums.Role;
import fr.springboot.backend.model.User;

/**
 * Utility class for role-based permissions management.
 * Handles authorization checks for user operations based on roles and business rules.
 */
public class RolePermissions {

    private final User currentUser;
    private final User targetUser;

    /**
     * Constructor for permission checks involving both current and target users.
     *
     * @param currentUser the user performing the action
     * @param targetUser the user being acted upon
     */
    public RolePermissions(User currentUser, User targetUser) {
        this.currentUser = currentUser;
        this.targetUser = targetUser;
    }

    /**
     * Constructor for permission checks involving only the current user.
     *
     * @param currentUser the user performing the action
     */
    public RolePermissions(User currentUser) {
        this.currentUser = currentUser;
        this.targetUser = null;
    }

    // ========== ROLE CHECKS ==========

    /**
     * Checks if the current user is an administrator.
     *
     * @return true if the user is an administrator, false otherwise
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == Role.ADMINISTRATEUR;
    }

    /**
     * Checks if the current user belongs to the HR department.
     *
     * @return true if the user is in HR department, false otherwise
     */
    public boolean isRH() {
        return currentUser != null &&
                currentUser.getDepartment() != null &&
                "Ressources Humaines".equalsIgnoreCase(currentUser.getDepartment().getName());
    }

    /**
     * Checks if the current user is the head of the HR department.
     *
     * @return true if the user is HR department head, false otherwise
     */
    public boolean isDepartmentHeadRH() {
        return currentUser != null &&
                currentUser.getRole() == Role.CHEF_DEPARTEMENT &&
                isRH();
    }

    /**
     * Checks if the current user is an HR employee.
     *
     * @return true if the user is an HR employee, false otherwise
     */
    public boolean isEmployeRH() {
        return currentUser != null &&
                currentUser.getRole() == Role.EMPLOYE &&
                isRH();
    }

    // ========== VIEW PERMISSIONS ==========

    /**
     * Checks if the current user can view all users.
     *
     * @return true if the user can view all users, false otherwise
     */
    public boolean canViewAllUsers() {
        if (currentUser == null) return false;
        return isAdmin() || currentUser.getRole() == Role.CHEF_DEPARTEMENT || isRH();
    }

    /**
     * Checks if the current user can access the user list.
     *
     * @return true if the user can access user list, false otherwise
     */
    public boolean canAccessUserList() {
        return canViewAllUsers();
    }

    /**
     * Checks if the current user can view private information of the target user.
     *
     * @return true if the user can view private info, false otherwise
     */
    public boolean canViewPrivateInfo() {
        if (currentUser == null || targetUser == null) return false;

        // Admin can see everything
        if (isAdmin()) return true;

        // HR department head can see private information
        if (isDepartmentHeadRH()) return true;

        // HR employee can see private information
        if (isEmployeRH()) return true;

        // Department head can see their own information
        if (currentUser.getRole() == Role.CHEF_DEPARTEMENT && isSelfEdit()) {
            return true;
        }

        // Otherwise no
        return false;
    }

    // ========== CREATION PERMISSIONS ==========

    /**
     * Checks if the current user can create users.
     *
     * @return true if the user can create users, false otherwise
     */
    public boolean canCreateUser() {
        if (currentUser == null) return false;
        return isAdmin() || currentUser.getRole() == Role.CHEF_DEPARTEMENT;
    }

    // ========== DELETION PERMISSIONS ==========

    /**
     * Checks if the current user can delete users.
     *
     * @return true if the user can delete users, false otherwise
     */
    public boolean canDeleteUser() {
        return isAdmin();
    }

    /**
     * Checks if the current user can delete the target user.
     *
     * @return true if the user can delete the target user, false otherwise
     */
    public boolean canDeleteUserWithTarget() {
        if (currentUser == null || targetUser == null) return false;

        // Only admin can delete
        if (!isAdmin()) return false;

        // Cannot delete yourself
        if (isSelfEdit()) return false;

        return true;
    }

    // ========== MODIFICATION PERMISSIONS ==========

    /**
     * Checks if the current user can update private information of the target user.
     *
     * @return true if the user can update private info, false otherwise
     */
    public boolean canUpdatePrivateInfo() {
        if (currentUser == null || targetUser == null) return false;

        // Admin can modify all private information
        if (isAdmin()) return true;

        // HR department head can modify private information
        if (isDepartmentHeadRH()) return true;

        // HR employee can modify private information
        if (isEmployeRH()) return true;

        return false;
    }

    /**
     * Checks if the current user can update public information of the target user.
     *
     * @return true if the user can update public info, false otherwise
     */
    public boolean canUpdatePublicInfo() {
        if (currentUser == null || targetUser == null) return false;

        // Admin can modify all public information
        if (isAdmin()) return true;

        // Department head can modify public information in their department
        if (currentUser.getRole() == Role.CHEF_DEPARTEMENT &&
                currentUser.getDepartment() != null &&
                targetUser.getDepartment() != null &&
                currentUser.getDepartment().getId().equals(targetUser.getDepartment().getId())) {
            return true;
        }

        // HR can modify public information
        if (isRH()) return true;

        return false;
    }

    /**
     * Checks if the current user can update the salary of the target user.
     *
     * @return true if the user can update salary, false otherwise
     */
    public boolean canUpdateSalary() {
        if (currentUser == null || targetUser == null) return false;

        // Admin can modify salaries EXCEPT their own
        if (isAdmin() && !isSelfEdit()) return true;

        // HR department head can modify salaries
        if (isDepartmentHeadRH()) return true;

        return false;
    }

    // ========== ASSIGNMENT PERMISSIONS ==========

    /**
     * Checks if the current user can assign the administrator role.
     *
     * @return true if the user can assign admin role, false otherwise
     */
    public boolean canAssignAdminRole() {
        return isAdmin();
    }

    /**
     * Checks if the current user can assign users to the HR department.
     *
     * @return true if the user can assign to HR department, false otherwise
     */
    public boolean canAssignRHDepartment() {
        if (currentUser == null) return false;
        return isAdmin() || isDepartmentHeadRH();
    }

    // ========== VALIDATIONS ==========

    /**
     * Validates role assignment with business rules.
     *
     * @param newRole the role to assign
     * @param newDepartmentId the department ID for the assignment
     * @return error message if invalid, null if valid
     */
    public String validateRoleAssignment(Role newRole, Integer newDepartmentId) {
        if (currentUser == null) {
            return "Utilisateur non authentifié";
        }

        // Only admin can assign ADMINISTRATOR role
        if (newRole == Role.ADMINISTRATEUR && !isAdmin()) {
            return "Seul un administrateur peut assigner le rôle ADMINISTRATEUR";
        }

        // If assigning DEPARTMENT_HEAD, verify there's not already a head in that department
        if (newRole == Role.CHEF_DEPARTEMENT && newDepartmentId != null) {
            // This verification should be done in the service with repository access
            // Returning null here, verification will be done in service
        }

        return null; // No error
    }

    /**
     * Validates department assignment with business rules.
     *
     * @param newDepartmentId the department ID to assign
     * @return error message if invalid, null if valid
     */
    public String validateDepartmentAssignment(Integer newDepartmentId) {
        if (currentUser == null) {
            return "Utilisateur non authentifié";
        }

        // Specific rules will be verified in the service
        return null;
    }

    // ========== UTILITY METHODS ==========

    /**
     * Checks if the current user is editing their own profile.
     *
     * @return true if editing own profile, false otherwise
     */
    public boolean isSelfEdit() {
        if (currentUser == null || targetUser == null) return false;
        return currentUser.getId().equals(targetUser.getId());
    }

    // ========== ERROR MESSAGES ==========

    /**
     * Generates an access denied message for a specific action.
     *
     * @param action the action that was denied
     * @return formatted access denied message
     */
    public String getAccessDeniedMessage(String action) {
        return String.format("Vous n'avez pas la permission de %s", action);
    }
}