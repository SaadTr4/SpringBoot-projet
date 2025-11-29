import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-projects',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.scss']
})
export class ProjectsComponent {

  username = "Admin";
  modalOpen = false;

  projects = [
    {
      id: 1,
      nom: "Migration Cloud",
      chefProjet: "Karim",
      statut: "En cours",
      employesAffectes: ["Jean", "Sophie", "Marc"]
    },
    {
      id: 2,
      nom: "Refonte Site Web",
      chefProjet: "Alice",
      statut: "Terminé",
      employesAffectes: ["Salim", "Laura"]
    },
    {
      id: 3,
      nom: "Automatisation",
      chefProjet: "Nadia",
      statut: "En cours",
      employesAffectes: ["Yassine", "Omar", "Claire"]
    }
  ];

  openModal() {
    this.modalOpen = true;
  }

  closeModal() {
    this.modalOpen = false;
  }
}
