import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { PermissionsService } from '../../services/permissions.service';
import { User } from '../../model/user.model';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-employees',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
  ],
  templateUrl: './employees.component.html',
  styleUrls: ['./employees.component.scss']
})
export class EmployeesComponent implements OnInit {

  employees: User[] = [];
  currentUser: User | null = null;

  // Listes déroulantes
  departments: any[] = [];
  positions: any[] = [];
  

  grades = [
    { value: 'JUNIOR', label: 'Junior' },
    { value: 'INTERMEDIAIRE', label: 'Intermédiaire' },
    { value: 'SENIOR', label: 'Senior' },
    { value: 'EXPERT', label: 'Expert' }
  ];

  contractTypes = [
    { value: 'PERMANENT_FULL_TIME', label: 'CDI Temps plein' },
    { value: 'PERMANENT_PART_TIME', label: 'CDI Temps partiel' },
    { value: 'FIXED_TERM_FULL_TIME', label: 'CDD Temps plein' },
    { value: 'FIXED_TERM_PART_TIME', label: 'CDD Temps partiel' },
    { value: 'TEMPORARY_AGENCY', label: 'Intérim' },
    { value: 'INTERNSHIP', label: 'Stage' },
    { value: 'APPRENTICESHIP', label: 'Alternance / Apprentissage' }
  ];

  roles = [
    { value: 'ADMINISTRATEUR', label: 'Administrateur' },
    { value: 'CHEF_DEPARTEMENT', label: 'Chef de Département' },
    { value: 'CHEF_PROJET', label: 'Chef de Projet' },
    { value: 'EMPLOYE', label: 'Employé' }
  ];

  // Recherche
  searchText = '';
  searchDepartment = 'all';
  searchPosition = 'all';
  searchRole = 'all';
  searchGrade = 'all';
  searchActive = false;
  searchCount = 0;

  // Modal
  modalOpen = false;
  modalMode: 'add' | 'edit' = 'add';
  selectedUser: User = {} as User;
  selectedImage: File | null = null;

  // Permissions générales
  canAdd = false;
  canEdit = false;
  canDelete = false;
  showFilter = true;

  constructor(
    private api: ApiService,
    private permissionsService: PermissionsService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    console.log('✅ EmployeesComponent initialized');
    this.loadCurrentUser();
  }

  /**
   * Charge l'utilisateur connecté et initialise les permissions
   */
  loadCurrentUser() {
    this.api.checkAuth().subscribe({
      next: (response) => {

              console.log('User from backend:', response); // <-- AJOUTE ICI

        // Construire l'objet currentUser
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



        // Charger les données
        this.loadEmployees();
        this.loadDepartments();
        this.loadPositions();
      },
      error: (err) => {
        console.error('Erreur authentification:', err);
        // Rediriger vers login si nécessaire
      }
    });
  }

  /**
   * Met à jour les permissions basées sur le rôle de l'utilisateur
   */
  updatePermissions() {
    if (!this.currentUser) {
      console.warn('Aucun utilisateur connecté');
      return;
    }

    // Utiliser le service de permissions
    const perms = this.permissionsService.getPermissions(this.currentUser);

    this.canAdd = perms.employees.canCreate;
    this.canEdit = perms.employees.canUpdate;
    this.canDelete = perms.employees.canDelete;

    console.log('Permissions chargées:', {
      role: this.currentUser.role,
      department: this.currentUser.department,
      canAdd: this.canAdd,
      canEdit: this.canEdit,
      canDelete: this.canDelete
    });
  }

  /**
   * Vérifie si on peut modifier un employé spécifique
   */
  canEditEmployee(employee: User): boolean {
    return this.permissionsService.canUpdateEmployee(this.currentUser, employee);
  }

  /**
   * Vérifie si on peut supprimer un employé spécifique
   */
  canDeleteEmployee(employee: User): boolean {
    return this.permissionsService.canDeleteEmployee(this.currentUser, employee);
  }

  /**
   * Vérifie si on peut voir les infos privées d'un employé
   */
  canViewPrivateInfo(employee: User): boolean {
    return this.permissionsService.canViewPrivateInfo(this.currentUser, employee);
  }

  /**
   * Vérifie si on peut modifier le salaire d'un employé
   */
  canEditSalary(employee: User): boolean {
    return this.permissionsService.canUpdateSalary(this.currentUser, employee);
  }

  // ==================== CHARGEMENT DES DONNÉES ====================

  loadDepartments() {
    this.api.getDepartments().subscribe({
      next: (data) => {
        this.departments = data;
        console.log('Départements chargés:', data.length);

      // Mettre à jour les permissions maintenant que le département est chargé
      this.updatePermissions();
      },
      error: (err) => console.error('❌ Erreur chargement départements:', err)
    });
  }

  loadPositions() {
    this.api.getPositions().subscribe({
      next: (data) => {
        this.positions = data;
        console.log('Postes chargés:', data.length);
      },
      error: (err) => console.error('❌ Erreur chargement postes:', err)
    });
  }

  loadEmployees() {
    this.api.getUsers().subscribe({
      next: (data) => {
        // Filtrer les employés selon les permissions
        if (this.currentUser) {
          this.employees = this.permissionsService.filterEmployeesByPermissions(
            this.currentUser,
            data
          );
          console.log(`${this.employees.length} employés autorisés sur ${data.length} total`);
        } else {
          this.employees = data;
        }
      },
      error: (err) => console.error('❌ Erreur chargement employés:', err)
    });
  }

  // ==================== MODAL ====================

  openModal(mode: 'add' | 'edit', user?: User) {
    this.modalMode = mode;

    if (mode === 'edit' && user) {
      this.selectedUser = { ...user };

      // Normalisation pour le modal
      if (this.selectedUser.grade) {
        const gradeFound = this.grades.find(g => g.label === this.selectedUser.grade || g.value === this.selectedUser.grade);
        this.selectedUser.grade = gradeFound ? gradeFound.value : '';
      }

      if (this.selectedUser.contractType) {
        const contractFound = this.contractTypes.find(ct => ct.label === this.selectedUser.contractType || ct.value === this.selectedUser.contractType);
        this.selectedUser.contractType = contractFound ? contractFound.value : '';
      }

      // Vérifier les permissions pour le champ salaire
      if (!this.canEditSalary(user)) {
        console.log('⚠️ Pas de droit de modification du salaire pour cet employé');
      }

    } else {
      this.selectedUser = {} as User;
    }

    this.modalOpen = true;
  }

  closeModal() {
    this.modalOpen = false;
    this.selectedUser = {} as User;
    this.selectedImage = null;
  }

  // ==================== GESTION IMAGE ====================

  onImageSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      // Vérifications
      if (file.size > 5 * 1024 * 1024) {
        alert('Le fichier est trop volumineux (max 5MB)');
        return;
      }
      if (!file.type.startsWith('image/')) {
        alert('Le fichier doit être une image');
        return;
      }
      this.selectedImage = file;
      console.log('📷 Image sélectionnée:', file.name);
    }
  }

  uploadImage(userId: number) {
    if (!this.selectedImage) return;

    const formData = new FormData();
    formData.append('image', this.selectedImage);

    this.http.post(`http://localhost:8080/api/users/${userId}/image`, formData, {
      withCredentials: true
    }).subscribe({
      next: () => {
        console.log('✅ Image uploadée avec succès');
        this.loadEmployees();
        this.closeModal();
      },
      error: (err) => {
        console.error('❌ Erreur upload image:', err);
        alert('Erreur lors de l\'upload de l\'image');
        this.loadEmployees();
        this.closeModal();
      }
    });
  }

  getUserImageUrl(userId: number): string {
    return `http://localhost:8080/api/users/${userId}/image?t=${new Date().getTime()}`;
  }

  hasImage(user: User): boolean {
    return user.hasImage !== false && user.id != null;
  }

  onImageError(user: User) {
    user.hasImage = false;
  }

  // ==================== CRUD ====================

  saveUser() {
    console.log('Sauvegarde utilisateur:', this.modalMode);

    // Validation des permissions
    if (this.modalMode === 'add' && !this.canAdd) {
      alert('Vous n\'avez pas la permission de créer un employé');
      return;
    }

    if (this.modalMode === 'edit') {
      if (!this.selectedUser.id) {
        alert('ID utilisateur manquant');
        return;
      }

      // Vérifier les permissions pour cet employé spécifique
      const targetUser = this.employees.find(e => e.id === this.selectedUser.id);
      if (targetUser && !this.canEditEmployee(targetUser)) {
        alert('Vous n\'avez pas la permission de modifier cet employé');
        return;
      }
    }

    if (this.modalMode === 'add') {
      this.api.createUser(this.selectedUser).subscribe({
        next: (user) => {
          console.log('✅ Employé créé:', user);
          if (this.selectedImage && user.id) {
            this.uploadImage(user.id);
          } else {
            this.loadEmployees();
            this.closeModal();
          }
        },
        error: (err) => {
          console.error('❌ Erreur création:', err);
          alert(err.error?.error || 'Erreur lors de la création');
        }
      });
    } else if (this.modalMode === 'edit' && this.selectedUser.id) {
      this.api.updateUser(this.selectedUser.id, this.selectedUser).subscribe({
        next: () => {
          console.log('✅ Employé modifié');
          if (this.selectedImage && this.selectedUser.id) {
            this.uploadImage(this.selectedUser.id);
          } else {
            this.loadEmployees();
            this.closeModal();
          }
        },
        error: (err) => {
          console.error('❌ Erreur modification:', err);
          alert(err.error?.error || 'Erreur lors de la modification');
        }
      });
    }
  }

  deleteUser(id: number) {
    // Vérifier les permissions
    const targetUser = this.employees.find(e => e.id === id);
    if (targetUser && !this.canDeleteEmployee(targetUser)) {
      alert('Vous n\'avez pas la permission de supprimer cet employé');
      return;
    }

    if (confirm('Confirmer la suppression de cet employé ?')) {
      this.api.deleteUser(id).subscribe({
        next: () => {
          console.log('✅ Employé supprimé');
          this.loadEmployees();
        },
        error: (err) => {
          console.error('❌ Erreur suppression:', err);
          alert('Erreur lors de la suppression');
        }
      });
    }
  }

  // ==================== RECHERCHE ====================

  searchUsers() {
    const params: any = {};

    if (this.searchText) params.searchText = this.searchText;
    if (this.searchRole && this.searchRole !== 'all') params.role = this.searchRole;
    if (this.searchDepartment && this.searchDepartment !== 'all') params.departmentId = this.searchDepartment;
    if (this.searchPosition && this.searchPosition !== 'all') params.positionId = this.searchPosition;
    if (this.searchGrade && this.searchGrade !== 'all') params.grade = this.searchGrade;

    console.log('🔍 Recherche avec paramètres:', params);

    this.api.searchUsers(params).subscribe({
      next: (data) => {
        this.employees = data;
        this.searchActive = true;
        this.searchCount = data.length;
        console.log(`✅ ${data.length} résultat(s) trouvé(s)`);
      },
      error: (err) => {
        console.error('❌ Erreur recherche:', err);
      }
    });
  }

  resetSearch() {
    this.searchText = '';
    this.searchRole = 'all';
    this.searchDepartment = 'all';
    this.searchPosition = 'all';
    this.searchGrade = 'all';
    this.searchActive = false;
    this.searchCount = 0;
    this.loadEmployees();
    console.log('🔄 Recherche réinitialisée');
  }

  // ==================== UTILITAIRES ====================

  trackByUserId(index: number, employee: User): number {
    return employee.id || index;
  }

  getRoleLabel(roleValue: string): string {
    const found = this.roles.find(r => r.value === roleValue);
    return found ? found.label : roleValue;
  }
}
