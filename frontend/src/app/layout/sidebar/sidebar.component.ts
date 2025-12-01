import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { PermissionsService } from '../../services/permissions.service';
import { User } from '../../model/user.model';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
})
export class SidebarComponent implements OnInit {

  currentUser: User | null = null;

  // Permissions pour afficher/masquer les menus
  canViewEmployees = false;
  canViewProjects = false;
  canViewDepartments = false;
  canViewPositions = false;
  canViewPayslips = false;

  constructor(
    private api: ApiService,
    private permissionsService: PermissionsService
  ) {}

  ngOnInit(): void {
    this.loadCurrentUser();
  }

  loadCurrentUser() {
    this.api.checkAuth().subscribe({
      next: (response) => {
        this.currentUser = {
          id: response.userId,
          matricule: response.matricule,
          role: response.role,
          firstName: response.firstName || '',
          lastName: response.lastName || '',
          fullName: response.fullName || `${response.firstName} ${response.lastName}`,
          department: response.department || '',
          departmentId: response.departmentId
        } as User;

        // Mettre à jour les permissions
        this.updatePermissions();
      },
      error: (err) => {
        console.error(' Erreur authentification sidebar:', err);
      }
    });
  }

  updatePermissions() {
    if (!this.currentUser) return;

    const perms = this.permissionsService.getPermissions(this.currentUser);

    // Permissions d'affichage des menus
    this.canViewEmployees = perms.employees.canViewAll;
    this.canViewProjects = perms.projects.canViewAll;
    this.canViewDepartments = perms.departments.canUpdate || perms.departments.canCreate;
    this.canViewPositions = perms.positions.canUpdate || perms.positions.canCreate;
    this.canViewPayslips = perms.payslips.canViewAll;
    this.canViewDepartments = this.permissionsService.canViewDepartments(this.currentUser);


    console.log(' Permissions sidebar:', {
      role: this.currentUser.role,
      department: this.currentUser.department,
      canViewEmployees: this.canViewEmployees,
      canViewProjects: this.canViewProjects,
      canViewDepartments: this.canViewDepartments,
      canViewPositions: this.canViewPositions,
      canViewPayslips: this.canViewPayslips
    });
  }
}
