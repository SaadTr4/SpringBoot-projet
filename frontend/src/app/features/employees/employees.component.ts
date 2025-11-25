import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-employees',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './employees.component.html',
  styleUrls: ['./employees.component.scss']
})
export class EmployeesComponent {

  username = "Admin";

  modalOpen = false;

  employees = [
    { id: 1, nom: "Dupont", prenom: "Jean", email: "jean.dupont@entreprise.com", role: "Développeur", departement: "Informatique", projet: "Projet X", salaire: 3500 },
    { id: 2, nom: "Martin", prenom: "Sophie", email: "sophie.martin@entreprise.com", role: "RH", departement: "Ressources Humaines", projet: "-", salaire: 3000 },
    { id: 3, nom: "Benali", prenom: "Karim", email: "karim.benali@entreprise.com", role: "Chef Projet", departement: "Informatique", projet: "Projet Alpha", salaire: 4800 }
  ];

  openModal() {
    this.modalOpen = true;
  }

  closeModal() {
    this.modalOpen = false;
  }
}
