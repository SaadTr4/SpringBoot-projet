import { Injectable } from '@angular/core';
import { User } from '../model/user.model';

/**
 * Service de gestion des permissions côté frontend
 * Réplique la logique de RolePermissions.java du backend
 */
@Injectable({
  providedIn: 'root'
})
export class PermissionsService {

  constructor() { }

  // ==================== VÉRIFICATIONS DE RÔLES ====================

  /**
   * Vérifie si l'utilisateur est administrateur
   */
  isAdmin(user: User | null): boolean {
    return user?.role === 'ADMINISTRATEUR';
  }

  /**
   * Vérifie si l'utilisateur est chef de département
   */
  isDepartmentHead(user: User | null): boolean {
    return user?.role === 'CHEF_DEPARTEMENT';
  }

  /**
   * Vérifie si l'utilisateur est chef de département RH
   */
  isDepartmentHeadRH(user: User | null): boolean {
    return this.isDepartmentHead(user) && this.isRHDepartment(user);
  }

  /**
   * Vérifie si l'utilisateur est employé RH
   */
  isEmployeeRH(user: User | null): boolean {
    return user?.role === 'EMPLOYE' && this.isRHDepartment(user);
  }

  /**
   * Vérifie si l'utilisateur est chef de projet
   */
  isProjectManager(user: User | null): boolean {
    return user?.role === 'CHEF_PROJET';
  }

  /**
   * Vérifie si l'utilisateur est dans le département RH
   */
  private isRHDepartment(user: User | null): boolean {
    return user?.department === 'Ressources Humaines' ||
           user?.department === 'RH' ||
           (user as any)?.departmentCode === 'RH';
  }

  // ==================== PERMISSIONS EMPLOYÉS ====================

  /**
   * Peut créer un employé
   * ADMIN:  CRUD complet
   * CHEF_DEPT:  Pas d'accès création
   * CHEF_PROJET:  Pas d'accès
   * EMPLOYE:  Pas d'accès
   * EMPLOYE RH:  Peut créer (sauf RH et Admin)
   */
  canCreateEmployee(user: User | null): boolean {
    return this.isAdmin(user) || this.isEmployeeRH(user);
  }

  /**
   * Peut voir tous les employés (accès à la liste)
   * ADMIN:  Tous
   * CHEF_DEPT:  Consultation (ne peut pas voir les admins)
   * CHEF_PROJET:  Consultation (employés classiques + chefs de projets uniquement)
   * EMPLOYE:  Aucun accès à la liste
   * EMPLOYE RH:  Consultation (sauf département RH et admin)
   */
  canViewAllEmployees(user: User | null): boolean {
    if (!user) return false;

    // ADMIN: voir tout
    if (this.isAdmin(user)) return true;

    // CHEF_DEPT: consultation (sauf admins)
    if (this.isDepartmentHead(user)) return true;

    // CHEF_PROJET: consultation (employés classiques + chefs projet)
    if (this.isProjectManager(user)) return true;

    // EMPLOYE RH: consultation (sauf RH et admin)
    if (this.isEmployeeRH(user)) return true;

    // EMPLOYE simple: aucun accès
    return false;
  }

  /**
   * Peut voir un employé spécifique dans la liste
   * Permet de filtrer qui apparaît dans la liste selon le rôle
   */
  canViewEmployee(currentUser: User | null, targetUser: User): boolean {
    if (!currentUser) return false;

    // ADMIN: voit tout
    if (this.isAdmin(currentUser)) return true;

    // CHEF_DEPT: ne peut pas voir les admins
    if (this.isDepartmentHead(currentUser)) {
      return targetUser.role !== 'ADMINISTRATEUR';
    }

    // CHEF_PROJET: voit seulement EMPLOYE et CHEF_PROJET
    if (this.isProjectManager(currentUser)) {
      return targetUser.role === 'EMPLOYE' || targetUser.role === 'CHEF_PROJET';
    }

    // EMPLOYE RH: voit tout sauf département RH et admins
    if (this.isEmployeeRH(currentUser)) {
      return targetUser.role !== 'ADMINISTRATEUR' &&
             !this.isRHDepartment(targetUser);
    }

    return false;
  }

  /**
   * Peut modifier un employé
   * ADMIN:  CRUD complet (sauf lui-même)
   * CHEF_DEPT:  Modification partielle (poste, grade) de son département
   * CHEF_PROJET:  Pas d'accès modification
   * EMPLOYE:  Ses propres infos (depuis Profile)
   * EMPLOYE RH:  Modification (sauf RH et admin)
   */
  canUpdateEmployee(currentUser: User | null, targetUser?: User): boolean {
    if (!currentUser) return false;

    // Vérifier qu'on ne modifie pas soi-même (sauf pour profil personnel)
    const isSelf = targetUser && currentUser.id === targetUser.id;

    // ADMIN: peut tout modifier sauf lui-même
    if (this.isAdmin(currentUser)) {
      return !isSelf;
    }

    // CHEF_DEPT: modification partielle de son département
    if (this.isDepartmentHead(currentUser)) {
      if (!targetUser) return true; // Pour l'affichage du bouton
      return targetUser.departmentId === currentUser.departmentId;
    }

    // EMPLOYE RH: modification (sauf RH et admin)
    if (this.isEmployeeRH(currentUser)) {
      if (!targetUser) return true;
      return targetUser.role !== 'ADMINISTRATEUR' &&
             !this.isRHDepartment(targetUser);
    }

    // EMPLOYE classique: seulement son profil
    if (isSelf) return true;

    return false;
  }

  /**
   * Peut supprimer un employé
   * ADMIN:  (sauf lui-même)
   * Autres:
   */
  canDeleteEmployee(currentUser: User | null, targetUser?: User): boolean {
    if (!currentUser) return false;

    // Seul l'admin peut supprimer
    if (!this.isAdmin(currentUser)) return false;

    // Ne peut pas se supprimer soi-même
    if (targetUser && currentUser.id === targetUser.id) return false;

    return true;
  }

  /**
   * Peut voir les informations privées d'un employé
   * (Salaire, informations sensibles)
   */
  canViewPrivateInfo(currentUser: User | null, targetUser?: User): boolean {
    if (!currentUser) return false;

    // ADMIN: voit tout
    if (this.isAdmin(currentUser)) return true;

    // EMPLOYE RH: voit les infos privées
    if (this.isEmployeeRH(currentUser)) return true;

    // Voir son propre profil
    if (targetUser && currentUser.id === targetUser.id) return true;

    return false;
  }

  /**
   * Peut modifier le salaire
   * ADMIN:  (sauf le sien)
   * EMPLOYE RH:  (sauf le sien)
   * Autres:
   */
  canUpdateSalary(currentUser: User | null, targetUser?: User): boolean {
    if (!currentUser) return false;

    // Ne peut pas modifier son propre salaire
    if (targetUser && currentUser.id === targetUser.id) return false;

    // Admin peut modifier les salaires
    if (this.isAdmin(currentUser)) return true;

    // Employé RH peut modifier les salaires
    if (this.isEmployeeRH(currentUser)) return true;

    return false;
  }

  /**
   * CHEF_DEPT peut modifier uniquement poste et grade
   * (Pas département, pas rôle, pas salaire)
   */
  canUpdateOnlyPositionAndGrade(user: User | null): boolean {
    return this.isDepartmentHead(user) && !this.isDepartmentHeadRH(user);
  }

  // ==================== PERMISSIONS PROJETS ====================

  /**
   * Peut créer un projet
   * ADMIN:  CRUD complet
   * CHEF_DEPT:  CRUD complet
   * CHEF_PROJET:  Pas de création
   * EMPLOYE:  Pas de création
   * EMPLOYE RH:  CRUD complet
   */
  canCreateProject(user: User | null): boolean {
    return this.isAdmin(user) ||
           this.isDepartmentHead(user) ||
           this.isEmployeeRH(user);
  }

  /**
   * Peut voir tous les projets
   * ADMIN:  Tous
   * CHEF_DEPT:  Tous
   * CHEF_PROJET:  Ses projets + ceux où il est assigné
   * EMPLOYE:  Ses projets + ceux où il est assigné
   * EMPLOYE RH:  Ses projets + ceux où il est assigné
   */
  canViewAllProjects(user: User | null): boolean {
    // Tous les rôles peuvent voir les projets
    // Mais la liste sera filtrée selon le rôle
    return user !== null;
  }

  /**
   * Peut voir un projet spécifique
   * Utilisé pour filtrer la liste des projets
   */
  canViewProject(currentUser: User | null, project: any): boolean {
    if (!currentUser) return false;

    // ADMIN et CHEF_DEPT: voient tous les projets
    if (this.isAdmin(currentUser) || this.isDepartmentHead(currentUser)) {
      return true;
    }

    // EMPLOYE RH: voit tous les projets
    if (this.isEmployeeRH(currentUser)) {
      return true;
    }

    // CHEF_PROJET et EMPLOYE: seulement leurs projets ou ceux où ils sont assignés
    // (Cette logique sera gérée côté composant avec les données du backend)
    return true; // Le filtre sera fait dans le composant
  }

  /**
   * Peut modifier un projet
   * ADMIN:  CRUD complet
   * CHEF_DEPT:  CRUD complet
   * CHEF_PROJET:  Peut assigner un autre chef de projet ou ajouter/retirer des employés
   * EMPLOYE:  Pas de modification
   * EMPLOYE RH:  CRUD complet
   */
  canUpdateProject(user: User | null): boolean {
    return this.isAdmin(user) ||
           this.isDepartmentHead(user) ||
           this.isProjectManager(user) ||
           this.isEmployeeRH(user);
  }


  canDeleteProject(user: User | null): boolean {
    return this.isAdmin(user) ||
           this.isDepartmentHead(user) ||
           this.isEmployeeRH(user);
  }

  /**
   * Peut affecter des employés à un projet
   * ADMIN:  (sauf admin et chef département)
   * CHEF_DEPT:  (sauf admin et chef département)
   * CHEF_PROJET:  Peut ajouter/retirer des employés
   * EMPLOYE:
   * EMPLOYE RH:  (sauf admin et chef département)
   */
  canAssignEmployeesToProject(user: User | null): boolean {
    return this.isAdmin(user) ||
           this.isDepartmentHead(user) ||
           this.isProjectManager(user) ||
           this.isEmployeeRH(user);
  }

  /**
   * Peut filtrer les projets
   * Tous peuvent filtrer, mais la liste affichée dépend des permissions
   */
  canFilterProjects(user: User | null): boolean {
    return user !== null;
  }

  // ==================== PERMISSIONS DÉPARTEMENTS ====================

  /**
   * Peut voir la liste des départements
   * ADMIN: oui
   * CHEF_DEPT: oui
   * EMPLOYE_RH: oui
   * Autres: non
   */
  canViewDepartments(user: User | null): boolean {
    if (!user) return false;

    return this.isAdmin(user)
        || this.isDepartmentHead(user)
        || this.isEmployeeRH(user);
  }

  /**
   * Peut voir un département spécifique
   * CHEF_DEPT : seulement son propre département
   * EMPLOYE RH : tous sauf RH
   * ADMIN : tout
   */
  canViewDepartment(currentUser: User | null, department: any): boolean {
    if (!currentUser) return false;

    if (this.isAdmin(currentUser)) return true;

    // Chef département → seulement son département
    if (this.isDepartmentHead(currentUser)) {
      return currentUser.departmentId === department.id;
    }

    // Employé RH → tous sauf RH
    if (this.isEmployeeRH(currentUser)) {
      return department.code !== 'RH';
    }

    return false;
  }

  /**
   * Peut créer un département
   * ADMIN uniquement
   */
  canCreateDepartment(user: User | null): boolean {
    return this.isAdmin(user);
  }

  /**
   * Peut mettre à jour un département
   * ADMIN → tout
   * CHEF_DEPT → uniquement son département
   * EMPLOYE RH → CRUD partiel sauf RH
   */
  canUpdateDepartment(currentUser: User | null, department?: any): boolean {
    if (!currentUser) return false;

    // ADMIN → tout
    if (this.isAdmin(currentUser)) return true;

    if (!department) return false;

    // CHEF_DEPT → son propre département
    if (this.isDepartmentHead(currentUser)) {
      return currentUser.departmentId === department.id;
    }

    // EMPLOYE RH → CRUD partiel mais : ne peut PAS modifier RH
    if (this.isEmployeeRH(currentUser)) {
      return department.code !== 'RH';
    }

    return false;
  }

  /**
   * Peut supprimer un département
   * ADMIN uniquement
   */
  canDeleteDepartment(user: User | null): boolean {
    return this.isAdmin(user);
  }


  // ==================== PERMISSIONS POSTES ====================

  /**
   * Peut créer un poste
   */
  canCreatePosition(user: User | null): boolean {
    return this.isAdmin(user);
  }

  /**
   * Peut modifier un poste
   */
  canUpdatePosition(user: User | null): boolean {
    return this.isAdmin(user);
  }

  /**
   * Peut supprimer un poste
   */
  canDeletePosition(user: User | null): boolean {
    return this.isAdmin(user);
  }

  // ==================== PERMISSIONS FICHES DE PAIE ====================

  /**
   * Peut voir toutes les fiches de paie
   */
  canViewAllPayslips(user: User | null): boolean {
    return this.isAdmin(user) ||
           this.isDepartmentHeadRH(user) ||
           this.isEmployeeRH(user);
  }

  /**
   * Peut créer une fiche de paie
   */
  canCreatePayslip(user: User | null): boolean {
    return this.isAdmin(user) ||
           this.isDepartmentHeadRH(user) ||
           this.isEmployeeRH(user);
  }

  /**
   * Peut modifier une fiche de paie
   */
  canUpdatePayslip(user: User | null): boolean {
    return this.isAdmin(user) ||
           this.isDepartmentHeadRH(user) ||
           this.isEmployeeRH(user);
  }

  /**
   * Peut supprimer une fiche de paie
   */
  canDeletePayslip(user: User | null): boolean {
    return this.isAdmin(user);
  }

  // ==================== UTILITAIRES ====================

  /**
   * Filtre les employés selon les permissions de l'utilisateur
   * Utilisé dans le composant pour afficher seulement les employés autorisés
   */
  filterEmployeesByPermissions(currentUser: User | null, employees: User[]): User[] {
    if (!currentUser) return [];

    return employees.filter(emp => this.canViewEmployee(currentUser, emp));
  }

  /**
   * Retourne un objet avec toutes les permissions pour l'UI
   */
  getPermissions(user: User | null): {
    employees: {
      canCreate: boolean;
      canViewAll: boolean;
      canUpdate: boolean;
      canDelete: boolean;
      canViewPrivateInfo: boolean;
      canUpdateSalary: boolean;
    };
    projects: {
      canCreate: boolean;
      canViewAll: boolean;
      canUpdate: boolean;
      canDelete: boolean;
      canFilter: boolean;
    };
    departments: {
      canCreate: boolean;
      canViewAll: boolean;
      canUpdate: boolean;
      canDelete: boolean;
    };

    positions: {
      canCreate: boolean;
      canUpdate: boolean;
      canDelete: boolean;
    };
    payslips: {
      canViewAll: boolean;
      canCreate: boolean;
      canUpdate: boolean;
      canDelete: boolean;
    };
  } {
    return {
      employees: {
        canCreate: this.canCreateEmployee(user),
        canViewAll: this.canViewAllEmployees(user),
        canUpdate: this.canUpdateEmployee(user),
        canDelete: this.canDeleteEmployee(user),
        canViewPrivateInfo: this.canViewPrivateInfo(user),
        canUpdateSalary: this.canUpdateSalary(user)
      },
      projects: {
        canCreate: this.canCreateProject(user),
        canViewAll: this.canViewAllProjects(user),
        canUpdate: this.canUpdateProject(user),
        canDelete: this.canDeleteProject(user),
        canFilter: this.canFilterProjects(user)
      },
      departments: {
        canCreate: this.canCreateDepartment(user),
        //canUpdate: this.canUpdateDepartment(user),
        canUpdate: true,
        canViewAll: this.canViewDepartments(user),
        canDelete: this.canDeleteDepartment(user)
      },
      positions: {
        canCreate: this.canCreatePosition(user),
        canUpdate: this.canUpdatePosition(user),
        canDelete: this.canDeletePosition(user)
      },
      payslips: {
        canViewAll: this.canViewAllPayslips(user),
        canCreate: this.canCreatePayslip(user),
        canUpdate: this.canUpdatePayslip(user),
        canDelete: this.canDeletePayslip(user)
      }
    };
  }
}
