import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { User } from '../../model/user.model';
import { FormsModule } from '@angular/forms';

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
  roles: string[] = ['ADMINISTRATEUR', 'RH', 'EMPLOYE', 'CHEF_PROJET']; // adapter si besoin

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

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadCurrentUser();
    this.loadEmployees();
  }

  loadCurrentUser() {
    this.api.getUsers().subscribe(users => {
      // Exemple : récupérer le premier utilisateur comme "currentUser"
      this.currentUser = users.length ? users[0] : null;
      this.updatePermissions();
    });
  }

  updatePermissions() {
    if (!this.currentUser) return;
    const role = this.currentUser.role;
    this.canAdd = role === 'ADMIN' || role === 'RH';
    this.canEdit = role === 'ADMIN' || role === 'RH';
    this.canDelete = role === 'ADMIN' || role === 'RH';
  }

  loadEmployees() {
    this.api.getUsers().subscribe(data => this.employees = data);
  }

  openModal(mode: 'add' | 'edit', user?: User) {
    this.modalMode = mode;
    this.selectedUser = mode === 'edit' && user ? { ...user } : {} as User;
    this.modalOpen = true;
  }

  closeModal() {
    this.modalOpen = false;
    this.selectedUser = {} as User;
  }

  saveUser() {
    if (this.modalMode === 'add') {
      this.api.createUser(this.selectedUser).subscribe(() => {
        this.loadEmployees();
        this.closeModal();
      });
    } else if (this.modalMode === 'edit' && this.selectedUser.id) {
      this.api.updateUser(this.selectedUser.id, this.selectedUser).subscribe(() => {
        this.loadEmployees();
        this.closeModal();
      });
    }
  }

  deleteUser(id: number) {
    if (confirm('Confirmer la suppression de cet employé ?')) {
      this.api.deleteUser(id).subscribe(() => this.loadEmployees());
    }
  }

  searchUsers() {
    const params: any = {};
    if (this.searchText) params.searchText = this.searchText;
    if (this.searchRole) params.role = this.searchRole;

    this.api.searchUsers(params).subscribe(data => this.employees = data);
  }

  resetSearch() {
    this.searchText = '';
    this.searchRole = '';
    this.loadEmployees();
  }
}
