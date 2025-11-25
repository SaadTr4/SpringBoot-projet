import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-departments',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './departments.component.html',
  styleUrls: ['./departments.component.scss']
})
export class DepartmentsComponent {

  username = "Admin";

  departments = [
    { id: 1, nom: "Ressources Humaines", chef: "Sophie Martin", nbEmployes: 12 },
    { id: 2, nom: "Informatique", chef: "Karim El Yazidi", nbEmployes: 35 },
    { id: 3, nom: "Marketing", chef: "Emma Dupont", nbEmployes: 18 },
    { id: 4, nom: "Production", chef: "Lucas Bernard", nbEmployes: 25 }
  ];

  openAddModal() {
    alert("Ajout futur ici");
  }

}
