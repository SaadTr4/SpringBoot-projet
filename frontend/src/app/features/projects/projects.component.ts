import { Component, OnInit, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { PermissionsService } from '../../services/permissions.service';
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
  filteredProjects: Project[] = [];
  users: User[] = [];
  currentUser: User | null = null;

  // Modal
  modalOpen = false;
  modalMode: 'add' | 'edit' = 'add';
  selectedProject: Project = {} as Project;

  // Permissions
  canAdd = false;
  canEdit = false;
  canDelete = false;
  canFilter = false;

  // Statuts disponibles
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

  constructor(
    private api: ApiService,
    @Inject(PermissionsService) private permissionsService: PermissionsService
  ) {}

  ngOnInit(): void {
    console.log('✅ ProjectsComponent initialized');
    this.loadCurrentUser();
  }

  /**
   * Charge l'utilisateur connecté et initialise les permissions
   */
  loadCurrentUser() {
    console.log('🔐 Vérification de l\'authentification...');
    this.api.checkAuth().subscribe({
      next: (response) => {
        console.log('✅ Authentification réussie:', response);

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

        console.log('👤 Utilisateur connecté:', this.currentUser);

        // Mettre à jour les permissions
        this.updatePermissions();

        // Charger les données
        this.loadUsers();
        this.loadProjects();
      },
      error: (err) => {
        console.error('❌ Erreur authentification:', err);
      }
    });
  }

  /**
   * Met à jour les permissions basées sur le rôle de l'utilisateur
   */
  updatePermissions() {
    if (!this.currentUser) {
      console.warn('⚠️ Aucun utilisateur connecté');
      return;
    }

    // Utiliser le service de permissions
    const perms = this.permissionsService.getPermissions(this.currentUser);

    this.canAdd = perms.projects.canCreate;
    this.canEdit = perms.projects.canUpdate;
    this.canDelete = perms.projects.canDelete;
    this.canFilter = perms.projects.canFilter;

    console.log('🔑 Permissions projets chargées:', {
      role: this.currentUser.role,
      canAdd: this.canAdd,
      canEdit: this.canEdit,
      canDelete: this.canDelete,
      canFilter: this.canFilter
    });
  }

  // ==================== CHARGEMENT DES DONNÉES ====================

  loadProjects() {
    this.api.getProjects().subscribe({
      next: (data) => {
        this.projects = data.map(p => ({
          ...p,
          projectManagerId: (p as any).projectManager?.id
        }));
        this.applyFilter();
        console.log('📋 Projets chargés:', data.length);
      },
      error: (err) => {
        console.error('❌ Erreur chargement projets:', err);
      }
    });
  }

  loadUsers() {
    this.api.getUsers().subscribe({
      next: (data) => {
        this.users = data;
        console.log('👥 Utilisateurs chargés:', data.length);
      },
      error: (err) => {
        console.error('❌ Erreur chargement utilisateurs:', err);
      }
    });
  }

  // ==================== MODAL ====================

  openModal(mode: 'add' | 'edit', project?: Project) {
    // Vérifier les permissions
    if (mode === 'add' && !this.canAdd) {
      alert('Vous n\'avez pas la permission de créer un projet');
      return;
    }

    if (mode === 'edit' && !this.canEdit) {
      alert('Vous n\'avez pas la permission de modifier un projet');
      return;
    }

    this.modalMode = mode;

    if (project) {
      this.selectedProject = { ...project };

      // Trouver l'ID du manager si on a le nom
      if (!this.selectedProject.projectManagerId && project.projectManagerName) {
        const manager = this.users.find(u =>
          `${u.firstName} ${u.lastName}` === project.projectManagerName
        );
        this.selectedProject.projectManagerId = manager?.id;
      }
    } else {
      this.selectedProject = {
        status: 'PLANNED' // Statut par défaut
      } as Project;
    }

    this.modalOpen = true;
    console.log('📝 Modal ouverte:', this.modalMode, this.selectedProject);
  }

  closeModal() {
    this.modalOpen = false;
    this.selectedProject = {} as Project;
    console.log('❌ Modal fermée');
  }

  // ==================== CRUD ====================

  saveProject(form: NgForm) {
    // Validation du formulaire
    if (form.invalid) {
      form.control.markAllAsTouched();
      alert('Veuillez remplir tous les champs obligatoires');
      return;
    }

    // Vérification des permissions
    if (this.modalMode === 'add' && !this.canAdd) {
      alert('Vous n\'avez pas la permission de créer un projet');
      return;
    }

    if (this.modalMode === 'edit' && !this.canEdit) {
      alert('Vous n\'avez pas la permission de modifier un projet');
      return;
    }

    console.log('💾 Sauvegarde projet:', this.modalMode, this.selectedProject);

    // Préparer le payload
    const payload: any = { ...this.selectedProject };

    // Attacher le manager
    if (this.selectedProject.projectManagerId) {
      payload.projectManager = this.users.find(u => u.id === this.selectedProject.projectManagerId);
    }

    if (this.modalMode === 'add') {
      // Créer un nouveau projet
      this.api.createProject(payload).subscribe({
        next: (result) => {
          console.log('✅ Projet créé:', result);
          this.loadProjects();
          this.closeModal();
        },
        error: (err) => {
          console.error('❌ Erreur création projet:', err);
          alert('Erreur lors de la création du projet');
        }
      });
    } else if (this.modalMode === 'edit' && this.selectedProject.id) {
      // Mettre à jour le chef de projet
      if (this.selectedProject.projectManagerId) {
        this.api.updateProjectManager(
          this.selectedProject.id,
          this.selectedProject.projectManagerId
        ).subscribe({
          next: () => {
            console.log('✅ Projet modifié');
            this.loadProjects();
            this.closeModal();
          },
          error: (err) => {
            console.error('❌ Erreur modification projet:', err);
            alert('Erreur lors de la modification du projet');
          }
        });
      }
    }
  }

  deleteProject(id: number) {
    // Vérifier les permissions
    if (!this.canDelete) {
      alert('Vous n\'avez pas la permission de supprimer un projet');
      return;
    }

    if (confirm('Confirmer la suppression de ce projet ?')) {
      console.log('🗑️ Suppression projet:', id);

      this.api.deleteProject(id).subscribe({
        next: () => {
          console.log('✅ Projet supprimé');
          this.loadProjects();
        },
        error: (err) => {
          console.error('❌ Erreur suppression projet:', err);
          alert('Erreur lors de la suppression du projet');
        }
      });
    }
  }

  // ==================== FILTRE ====================

  applyFilter() {
    if (!this.canFilter) {
      // Si pas de permission de filtre, afficher tous les projets
      this.filteredProjects = this.projects;
      return;
    }

    console.log('🔍 Application des filtres:', {
      name: this.filterName,
      managerId: this.filterManagerId,
      status: this.filterStatus
    });

    this.filteredProjects = this.projects.filter(p => {
      const matchesName = this.filterName
        ? p.name.toLowerCase().includes(this.filterName.toLowerCase())
        : true;

      const matchesManager = this.filterManagerId
        ? p.projectManagerId === this.filterManagerId
        : true;

      const matchesStatus = this.filterStatus
        ? p.status === this.filterStatus
        : true;

      return matchesName && matchesManager && matchesStatus;
    });

    console.log(`✅ ${this.filteredProjects.length} projet(s) filtré(s)`);
  }

  resetFilter() {
    this.filterName = '';
    this.filterManagerId = null;
    this.filterStatus = null;
    this.applyFilter();
    console.log('🔄 Filtres réinitialisés');
  }

  // ==================== UTILITAIRES ====================

  getStatusLabel(status?: string): string {
    if (!status) return '-';
    const found = this.statuses.find(s => s.value === status);
    return found ? found.label : status;
  }

  getManagerName(managerId?: number): string {
    if (!managerId) return '-';
    const manager = this.users.find(u => u.id === managerId);
    return manager ? `${manager.firstName} ${manager.lastName}` : '-';
  }

  trackByProjectId(index: number, project: Project): number {
    return project.id || index;
  }
}
