import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { User } from '../../model/user.model';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-employees',
  standalone: true,  // <-- rend le composant standalone
  imports: [
    CommonModule,
    FormsModule,       // <-- nécessaire pour ngModel
  ],
  templateUrl: './employees.component.html',
  styleUrls: ['./employees.component.scss']
})
export class EmployeesComponent implements OnInit {

  employees: User[] = [];

  // Ajout des propriétés pour les listes déroulantes
  departments: any[] = [];
  positions: any[] = [];
  grades = [
    { value: 'JUNIOR', label: 'Junior' },
    { value: 'SENIOR', label: 'Senior' },
    { value: 'EXPERT', label: 'Expert' }
  ];

  contractTypes = [
    { value: 'PERMANENT_FULL_TIME', label: 'CDI' },
    { value: 'PERMANENT_PART_TIME', label: 'CDI temps partiel' },
    { value: 'FIXED_TERM_FULL_TIME', label: 'CDD' },
    { value: 'FIXED_TERM_PART_TIME', label: 'CDD temps partiel' },
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


  // Pour la recherche avancée
  searchDepartment = '';
  searchPosition = '';
  searchGrade = '';
  searchActive = false;
  searchCount = 0;
  currentUser: User | null = null;

  // Modal
  modalOpen = false;
  modalMode: 'add' | 'edit' = 'add';
  selectedUser: User = {} as User;

  // Permissions
  canAdd = false;
  canEdit = false;
  canDelete = false;

  // Recherche
  showFilter = true;
  searchText = '';
  searchRole = '';

  constructor(
  private api: ApiService,
  private http: HttpClient 
) {}

ngOnInit(): void {
  console.log('EmployeesComponent initialized');
  this.loadCurrentUser();

}

loadDepartments() {
  this.api.getDepartments().subscribe(data => this.departments = data);
}

loadPositions() {
  this.api.getPositions().subscribe(data => this.positions = data);
}

loadCurrentUser() {
  console.log('Appel checkAuth');
  this.api.checkAuth().subscribe({
    next: (response) => {
      console.log('checkAuth response:', response);
      this.currentUser = {
        id: response.userId,
        role: response.role,
        matricule: response.matricule,
        firstName: response.firstName || '',
        lastName: response.lastName || '',
        department: response.department || ''
      } as User;
      this.updatePermissions();
      
      this.loadEmployees(); 
      this.loadDepartments();
      this.loadPositions();
    },
    error: (err) => {
      console.error('Erreur auth:', err);
      // Gérer la redirection ou l'affichage de l'erreur
    }
  });
}

updatePermissions() {
  if (!this.currentUser) return;

  console.log('Current User:', this.currentUser);

  const role = this.currentUser.role;
  const isRH = this.currentUser.department === 'RH';

  this.canAdd = role === 'ADMINISTRATEUR' || (role === 'CHEF_DEPARTEMENT' && isRH);
  this.canEdit = role === 'ADMINISTRATEUR' || role === 'CHEF_DEPARTEMENT' || isRH;
  this.canDelete = role === 'ADMINISTRATEUR' || (role === 'CHEF_DEPARTEMENT' && isRH);

  console.log('Permissions:', { canAdd: this.canAdd, canEdit: this.canEdit, canDelete: this.canDelete });
}


  trackByUserId(index: number, employee: User): number {
    return employee.id || index;
  }

  private isRH(): boolean {
    return this.currentUser?.department === 'RH';
  }

  private isDepartmentHeadRH(): boolean {
    return this.currentUser?.role === 'CHEF_DEPARTEMENT' && this.isRH();
  }

  loadEmployees() {
    this.api.getUsers().subscribe(data => this.employees = data);
  }

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

    } else {
      this.selectedUser = {} as User;
    }

    this.modalOpen = true;
  }


  closeModal() {
    this.modalOpen = false;
    this.selectedUser = {} as User;
    this.selectedImage = null; // Réinitialiser l'image
  }

  selectedImage: File | null = null;

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
    }
  }


  saveUser() {
    if (this.modalMode === 'add') {
      this.api.createUser(this.selectedUser).subscribe({
        next: (user) => {
          // Si une image a été sélectionnée, l'uploader
          if (this.selectedImage && user.id) {
            this.uploadImage(user.id);
          } else {
            this.loadEmployees();
            this.closeModal();
          }
        },
        error: (err) => {
          console.error('Erreur création:', err);
          alert(err.error?.error || 'Erreur lors de la création');
        }
      });
    } else if (this.modalMode === 'edit' && this.selectedUser.id) {
      this.api.updateUser(this.selectedUser.id, this.selectedUser).subscribe({
        next: () => {
          // Si une nouvelle image a été sélectionnée
          if (this.selectedImage && this.selectedUser.id) {
            this.uploadImage(this.selectedUser.id);
          } else {
            this.loadEmployees();
            this.closeModal();
          }
        },
        error: (err) => {
          console.error('Erreur modification:', err);
          alert(err.error?.error || 'Erreur lors de la modification');
        }
      });
    }
  }

  uploadImage(userId: number) {
    if (!this.selectedImage) return;
    
    const formData = new FormData();
    formData.append('image', this.selectedImage);
    
    // Utiliser HttpClient directement pour l'upload
    this.http.post(`http://localhost:8080/api/users/${userId}/image`, formData, {
      withCredentials: true
    }).subscribe({
      next: () => {
        this.loadEmployees();
        this.closeModal();
      },
      error: (err) => {
        console.error('Erreur upload image:', err);
        alert('Erreur lors de l\'upload de l\'image');
        this.loadEmployees();
        this.closeModal();
      }
    });
  }

  deleteUser(id: number) {
    if (confirm('Confirmer la suppression de cet employé ?')) {
      this.api.deleteUser(id).subscribe(() => this.loadEmployees());
    }
  }

  searchUsers() {
    const params: any = {};
    
    if (this.searchText) params.searchText = this.searchText;
    if (this.searchRole && this.searchRole !== 'all') params.role = this.searchRole;
    if (this.searchDepartment && this.searchDepartment !== 'all') params.departmentId = this.searchDepartment;
    if (this.searchPosition && this.searchPosition !== 'all') params.positionId = this.searchPosition;
    if (this.searchGrade && this.searchGrade !== 'all') params.grade = this.searchGrade;

    this.api.searchUsers(params).subscribe({
      next: (data) => {
        this.employees = data;
        this.searchActive = true;
        this.searchCount = data.length;
      },
      error: (err) => {
        console.error('Erreur recherche:', err);
      }
    });
  }

  resetSearch() {
    this.searchText = '';
    this.searchRole = '';
    this.searchDepartment = '';
    this.searchPosition = '';
    this.searchGrade = '';
    this.searchActive = false;
    this.searchCount = 0;
    this.loadEmployees();
  }
  
  getUserImageUrl(userId: number): string {
    return `http://localhost:8080/api/users/${userId}/image`;
  }

hasImage(user: User): boolean {
  return user.hasImage !== false && user.id != null;
}
getRoleLabel(roleValue: string): string {
  const found = this.roles.find(r => r.value === roleValue);
  return found ? found.label : roleValue;
}
onImageError(user: User) {
  // Si l'image échoue, on marque l'utilisateur comme "pas d'image"
  user.hasImage = false;
}

}
