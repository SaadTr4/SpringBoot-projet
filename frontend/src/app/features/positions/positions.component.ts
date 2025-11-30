import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { Position } from '../../model/position.model';
import { User } from  '../../model/user.model';

@Component({
  selector: 'app-poste',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './position.component.html',
  styleUrls: ['./positions.component.scss']
})
export class PositionsComponent implements OnInit {
  postes: Position[] = [];
  modalOpen = false;
  modalMode: 'add' | 'edit' = 'add';
  selectedPoste: Position = { id: 0, name: '', description: '' };
  users: User[] = []; // si on veut afficher les utilisateurs assignés

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadPositions();
  }

  loadPositions() {
    this.api.getPositions().subscribe(res => this.postes = res);
  }

  openAddModal() {
    this.modalMode = 'add';
    this.selectedPoste = { id: 0, name: '', description: '' };
    this.modalOpen = true;
  }

  openEditModal(poste: Position) {
    this.modalMode = 'edit';
    this.selectedPoste = { ...poste };
    this.modalOpen = true;
  }

  closeModal() {
    this.modalOpen = false;
  }

  savePoste() {
    if (this.modalMode === 'add') {
      this.api.createPosition(this.selectedPoste).subscribe(() => this.loadPositions());
    } else {
      this.api.updatePosition(this.selectedPoste.id!, this.selectedPoste).subscribe(() => this.loadPositions());
    }
    this.closeModal();
  }

  deletePoste(poste: Position) {
    if (confirm(`Voulez-vous vraiment supprimer le poste "${poste.name}" ?`)) {
      this.api.deletePosition(poste.id!).subscribe(() => this.loadPositions());
    }
  }

  viewUsers(poste: Position) {
    this.api.getUsersOfPosition(poste.id!).subscribe(users => {
      this.users = users;
      console.log(`Utilisateurs du poste "${poste.name}"`, users);
      // ici on peut ouvrir un modal pour afficher la liste des utilisateurs
    });
  }
}
