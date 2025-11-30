import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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

  username = 'Admin'; // à récupérer depuis Auth si besoin
  modalOpen = false;
  modalMode: 'add' | 'edit' = 'add';
  selectedProject: Project = {} as Project;

  projects: Project[] = [];
  users: User[] = []; // liste des utilisateurs pour assigner un chef ou membres

  constructor(private api: ApiService) { }

  ngOnInit(): void {
    this.loadProjects();
    this.loadUsers();
  }

  loadProjects() {
    this.api.getProjects().subscribe(data => this.projects = data);
  }

  loadUsers() {
    this.api.getUsers().subscribe(data => this.users = data);
  }

  openModal(mode: 'add' | 'edit', project?: Project) {
    this.modalMode = mode;
    this.selectedProject = project ? { ...project } : {} as Project;
    this.modalOpen = true;
  }

  closeModal() {
    this.modalOpen = false;
    this.selectedProject = {} as Project;
  }

  saveProject() {
    if (this.modalMode === 'add') {
      this.api.createProject(this.selectedProject).subscribe(() => {
        this.loadProjects();
        this.closeModal();
      });
    } else if (this.modalMode === 'edit' && this.selectedProject.id) {
      // ici on peut juste mettre à jour manager ou status si besoin
      this.loadProjects();
      this.closeModal();
    }
  }

  deleteProject(id: number) {
    if (confirm('Confirmer la suppression de ce projet ?')) {
      this.api.deleteProject(id).subscribe(() => this.loadProjects());
    }
  }

  filterProjects(name?: string, managerMatricule?: string, status?: string) {
    const params: any = {};
    if (name) params.name = name;
    if (managerMatricule) params.managerMatricule = managerMatricule;
    if (status) params.status = status;

    this.api.filterProjects(params).subscribe(data => this.projects = data);
  }
}
