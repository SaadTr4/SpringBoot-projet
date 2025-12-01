import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { Project } from '../../model/project.model';
import { User } from '../../model/user.model';

@Component({
  selector: 'app-projects',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.scss']
})
export class ProjectsComponent implements OnInit {

  projects: Project[] = [];
  users: User[] = [];
  filteredProjects: Project[] = [];

  currentUser: User | null = null;

  modalOpen = false;
  modalMode: 'add' | 'edit' = 'add';
  selectedProject: Project = {} as Project;

  // Permissions
  canAdd = false;
  canEdit = false;
  canDelete = false;
  canFilter = false;

  // Pour affichage statut
  statuses = [
    { value: 'PLANNED', label: 'Planifié' },
    { value: 'IN_PROGRESS', label: 'En cours' },
    { value: 'COMPLETED', label: 'Terminé' },
    { value: 'CANCELLED', label: 'Annulé' }
  ];

  // Filtres
  filterName = '';
  filterManagerId: number | null = null;
  filterStatus: string | null = null;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadCurrentUser();
    this.loadUsers();
    this.loadProjects();
  }

  loadCurrentUser() {
    this.api.checkAuth().subscribe({
      next: res => {
        this.currentUser = {
          id: res.userId,
          role: res.role,
          firstName: res.firstName,
          lastName: res.lastName
        } as User;
        this.updatePermissions();
      },
      error: err => console.error('Erreur auth:', err)
    });
  }

  updatePermissions() {
    if (!this.currentUser) return;
    const role = this.currentUser.role;
    this.canAdd = role === 'ADMINISTRATEUR';
    this.canEdit = role === 'ADMINISTRATEUR';
    this.canDelete = role === 'ADMINISTRATEUR';
    this.canFilter = role === 'ADMINISTRATEUR' || role === 'CHEF_DEPARTEMENT' || role === 'EMPLOYE';
  }

  loadProjects() {
    this.api.getProjects().subscribe({
      next: data => {
        this.projects = data.map(p => ({
          ...p,
          projectManagerId: (p as any).projectManager?.id
        }));
        this.applyFilter();
      },
      error: err => console.error('Erreur chargement projets:', err)
    });
  }

  loadUsers() {
    this.api.getUsers().subscribe({
      next: data => this.users = data,
      error: err => console.error('Erreur chargement utilisateurs:', err)
    });
  }

  openModal(mode: 'add' | 'edit', project?: Project) {
    this.modalMode = mode;
    if (project) {
      this.selectedProject = { ...project };
      if (!this.selectedProject.projectManagerId && project.projectManagerName) {
        const manager = this.users.find(u => `${u.firstName} ${u.lastName}` === project.projectManagerName);
        this.selectedProject.projectManagerId = manager?.id;
      }
    } else {
      this.selectedProject = {} as Project;
    }
    this.modalOpen = true;
  }

  closeModal() {
    this.modalOpen = false;
    this.selectedProject = {} as Project;
  }

  saveProject(form: NgForm) {
    if (form.invalid) {
      form.control.markAllAsTouched();
      return;
    }

    const payload: any = { ...this.selectedProject };
    payload.projectManager = this.users.find(u => u.id === this.selectedProject.projectManagerId);

    if (this.modalMode === 'add') {
      this.api.createProject(payload).subscribe(() => {
        this.loadProjects();
        this.closeModal();
      });
    } else if (this.modalMode === 'edit' && this.selectedProject.id) {
      this.api.updateProjectManager(this.selectedProject.id, this.selectedProject.projectManagerId!)
        .subscribe(() => {
          this.loadProjects();
          this.closeModal();
        });
    }
  }

  deleteProject(id: number) {
    if (confirm('Confirmer la suppression de ce projet ?')) {
      this.api.deleteProject(id).subscribe(() => this.loadProjects());
    }
  }

  getStatusLabel(status?: string): string {
    return this.statuses.find(s => s.value === status)?.label || '-';
  }

  /** ======= FILTRE ======= */
  applyFilter() {
    this.filteredProjects = this.projects.filter(p => {
      const matchesName = this.filterName ? p.name.toLowerCase().includes(this.filterName.toLowerCase()) : true;
      const matchesManager = this.filterManagerId ? p.projectManagerId === this.filterManagerId : true;
      const matchesStatus = this.filterStatus ? p.status === this.filterStatus : true;
      return matchesName && matchesManager && matchesStatus;
    });
  }

  resetFilter() {
    this.filterName = '';
    this.filterManagerId = null;
    this.filterStatus = null;
    this.applyFilter();
  }
}
