import { Component, OnInit } from '@angular/core';
import { ProjectService } from './project.service';
import { ProjectDTO } from './project.model';
import { CommonModule } from '@angular/common'; // nécessaire pour *ngFor


@Component({
  selector: 'app-projects',
  templateUrl: './project.component.html',
  standalone: true,
  imports: [CommonModule],
})
export class ProjectComponent implements OnInit {
  projects: ProjectDTO[] = [];
  selectedProject: ProjectDTO | null = null;


  constructor(private projectService: ProjectService) {}

  ngOnInit(): void {
    this.loadProjects();
  }

  loadProjects() {
    this.projectService.getAll().subscribe(data => this.projects = data);
  }

  deleteProject(id: number | undefined) {
    if (id === undefined) return;
    this.projectService.delete(id).subscribe(() => this.loadProjects());
  }
    // ✅ Nouvelle méthode pour modifier un projet
  editProject(project: ProjectDTO) {
    this.selectedProject = { ...project }; // on clone le projet pour l'éditer
    console.log('Projet sélectionné pour modification:', this.selectedProject);
    // ici tu peux ouvrir un formulaire/modal pour éditer le projet
  }

  // Méthode pour sauvegarder les modifications
  saveProject() {
    if (!this.selectedProject) return;
    this.projectService.create(this.selectedProject).subscribe(() => {
      this.loadProjects();
      this.selectedProject = null;
    });
  }
}
