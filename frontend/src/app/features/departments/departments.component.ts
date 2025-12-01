import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { Department, DepartmentDTO } from '../../model/department.model';

@Component({
  selector: 'app-departments',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './departments.component.html',
  styleUrls: ['./departments.component.scss']
})
export class DepartmentsComponent implements OnInit {

  username = "Admin";
  modalOpen = false;
  modalMode: 'add' | 'edit' = 'add';

  departments: DepartmentDTO[] = [];

  // Model pour le formulaire
  selectedDepartment: Partial<DepartmentDTO> = {};

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadDepartments();
  }

  loadDepartments() {
    this.api.getDepartmentsDTO().subscribe({
      next: (data) => {
        this.departments = data.filter(d => d && d.id != null);
      },
      error: (err) => console.error(err)
    });
  }
  

  openAddModal() {
    this.modalMode = 'add';
    this.selectedDepartment = {};
    this.modalOpen = true;
  }

  openEditModal(dept: Department) {
    this.modalMode = 'edit';
    this.selectedDepartment = { ...dept };
    this.modalOpen = true;
  }

  closeModal() {
    this.modalOpen = false;
  }

  saveDepartment() {
    if (!this.selectedDepartment.name || !this.selectedDepartment.code) return;

    if (this.modalMode === 'add') {
      this.api.createDepartment(this.selectedDepartment).subscribe({
        next: (d) => {
          this.departments.push(d);
          this.closeModal();
        },
        error: (err) => console.error(err)
      });
    } else {
      this.api.updateDepartment(this.selectedDepartment.id!, this.selectedDepartment).subscribe({
        next: (d) => {
          const index = this.departments.findIndex(dep => dep.id === d.id);
          if (index > -1) this.departments[index] = d;
          this.closeModal();
        },
        error: (err) => console.error(err)
      });
    }
  }

  deleteDepartment(id: number) {
    if (!confirm('Voulez-vous vraiment supprimer ce département ?')) return;

    this.api.deleteDepartment(id).subscribe({
      next: () => this.departments = this.departments.filter(d => d.id !== id),
      error: (err) => console.error(err)
    });
  }
}
