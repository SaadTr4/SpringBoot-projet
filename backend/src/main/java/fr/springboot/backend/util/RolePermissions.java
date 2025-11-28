package fr.springboot.backend.util;


import fr.springboot.backend.enums.Role;
import fr.springboot.backend.model.User;

public class RolePermissions {

    private final User currentUser;
    private final User targetUser;

    public RolePermissions(User currentUser, User targetUser) {
        this.currentUser = currentUser;
        this.targetUser = targetUser;
    }

    public RolePermissions(User currentUser) {
        this.currentUser = currentUser;
        this.targetUser = null;
    }

    // ========== VÉRIFICATIONS DE RÔLE ==========

    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == Role.ADMINISTRATEUR;
    }

    public boolean isRH() {
        return currentUser != null &&
                currentUser.getDepartment() != null &&
                "Ressources Humaines".equalsIgnoreCase(currentUser.getDepartment().getName());
    }

    public boolean isDepartmentHeadRH() {
        return currentUser != null &&
                currentUser.getRole() == Role.CHEF_DEPARTEMENT &&
                isRH();
    }

    public boolean isEmployeRH() {
        return currentUser != null &&
                currentUser.getRole() == Role.EMPLOYE &&
                isRH();
    }

    // ========== PERMISSIONS DE VISUALISATION ==========

    public boolean canViewAllUsers() {
        if (currentUser == null) return false;
        return isAdmin() || currentUser.getRole() == Role.CHEF_DEPARTEMENT || isRH();
    }

    public boolean canAccessUserList() {
        return canViewAllUsers();
    }

    public boolean canViewPrivateInfo() {
        if (currentUser == null || targetUser == null) return false;

        // Admin peut tout voir
        if (isAdmin()) return true;

        // Chef département RH peut voir les infos privées
        if (isDepartmentHeadRH()) return true;

        // Employé RH peut voir les infos privées
        if (isEmployeRH()) return true;

        // Chef département peut voir ses propres infos
        if (currentUser.getRole() == Role.CHEF_DEPARTEMENT && isSelfEdit()) {
            return true;
        }

        // Sinon non
        return false;
    }

    // ========== PERMISSIONS DE CRÉATION ==========

    public boolean canCreateUser() {
        if (currentUser == null) return false;
        return isAdmin() || currentUser.getRole() == Role.CHEF_DEPARTEMENT;
    }

    // ========== PERMISSIONS DE SUPPRESSION ==========

    public boolean canDeleteUser() {
        return isAdmin();
    }

    public boolean canDeleteUserWithTarget() {
        if (currentUser == null || targetUser == null) return false;

        // Seul l'admin peut supprimer
        if (!isAdmin()) return false;

        // On ne peut pas se supprimer soi-même
        if (isSelfEdit()) return false;

        return true;
    }

    // ========== PERMISSIONS DE MODIFICATION ==========

    public boolean canUpdatePrivateInfo() {
        if (currentUser == null || targetUser == null) return false;

        // Admin peut modifier toutes les infos privées
        if (isAdmin()) return true;

        // Chef département RH peut modifier les infos privées
        if (isDepartmentHeadRH()) return true;

        // Employé RH peut modifier les infos privées
        if (isEmployeRH()) return true;

        return false;
    }

    public boolean canUpdatePublicInfo() {
        if (currentUser == null || targetUser == null) return false;

        // Admin peut modifier toutes les infos publiques
        if (isAdmin()) return true;

        // Chef département peut modifier les infos publiques de son département
        if (currentUser.getRole() == Role.CHEF_DEPARTEMENT &&
                currentUser.getDepartment() != null &&
                targetUser.getDepartment() != null &&
                currentUser.getDepartment().getId().equals(targetUser.getDepartment().getId())) {
            return true;
        }

        // RH peut modifier les infos publiques
        if (isRH()) return true;

        return false;
    }

    public boolean canUpdateSalary() {
        if (currentUser == null || targetUser == null) return false;

        // Admin peut modifier les salaires SAUF le sien
        if (isAdmin() && !isSelfEdit()) return true;

        // Chef département RH peut modifier les salaires
        if (isDepartmentHeadRH()) return true;

        return false;
    }

    // ========== PERMISSIONS D'ASSIGNATION ==========

    public boolean canAssignAdminRole() {
        return isAdmin();
    }

    public boolean canAssignRHDepartment() {
        if (currentUser == null) return false;
        return isAdmin() || isDepartmentHeadRH();
    }

    // ========== VALIDATIONS ==========

    public String validateRoleAssignment(Role newRole, Integer newDepartmentId) {
        if (currentUser == null) {
            return "Utilisateur non authentifié";
        }

        // Seul l'admin peut assigner le rôle ADMINISTRATEUR
        if (newRole == Role.ADMINISTRATEUR && !isAdmin()) {
            return "Seul un administrateur peut assigner le rôle ADMINISTRATEUR";
        }

        // Si on assigne CHEF_DEPARTEMENT, vérifier qu'il n'y a pas déjà un chef dans ce département
        if (newRole == Role.CHEF_DEPARTEMENT && newDepartmentId != null) {
            // Cette vérification devra être faite dans le service avec accès au repository
            // On retourne null ici, la vérification se fera côté service
        }

        return null; // Pas d'erreur
    }

    public String validateDepartmentAssignment(Integer newDepartmentId) {
        if (currentUser == null) {
            return "Utilisateur non authentifié";
        }

        // Les règles spécifiques seront vérifiées dans le service
        return null;
    }

    // ========== UTILITAIRES ==========

    public boolean isSelfEdit() {
        if (currentUser == null || targetUser == null) return false;
        return currentUser.getId().equals(targetUser.getId());
    }

    // ========== MESSAGES D'ERREUR ==========

    public String getAccessDeniedMessage(String action) {
        return String.format("Vous n'avez pas la permission de %s", action);
    }
}
