import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { Department } from '../../model/department.model';
import { FormsModule } from '@angular/forms';

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

  departments: Department[] = [];

  // Model pour le formulaire
  newDepartment: Partial<Department> = {};

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadDepartments();
  }

  loadDepartments() {
    this.api.getDepartments().subscribe({
      next: (data) => this.departments = data,
      error: (err) => console.error(err)
    });
  }

  openAddModal() {
    this.newDepartment = {};
    this.modalOpen = true;
  }

  closeModal() {
    this.modalOpen = false;
  }

  saveDepartment() {
    if (!this.newDepartment.name || !this.newDepartment.code) return;

    this.api.createDepartment(this.newDepartment).subscribe({
      next: (d) => {
        this.departments.push(d);
        this.closeModal();
      },
      error: (err) => console.error(err)
    });
  }

  deleteDepartment(id: number) {
    if (!confirm('Voulez-vous vraiment supprimer ce département ?')) return;

    this.api.deleteDepartment(id).subscribe({
      next: () => this.departments = this.departments.filter(d => d.id !== id),
      error: (err) => console.error(err)
    });
  }
}
