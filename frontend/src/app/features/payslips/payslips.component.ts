import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-payslips',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './payslips.component.html',
  styleUrls: ['./payslips.component.scss']
})
export class PayslipsComponent {

  username = "Admin";
  modalOpen = false;

  payslips = [
    { id: 1, employeNom: "Jean Dupont", salaireBase: 3500, prime: 300, deduction: 50, total: 3750 },
    { id: 2, employeNom: "Sophie Martin", salaireBase: 3000, prime: 150, deduction: 0, total: 3150 },
    { id: 3, employeNom: "Karim Benali", salaireBase: 4800, prime: 400, deduction: 100, total: 5100 }
  ];

  openModal() {
    this.modalOpen = true;
  }

  closeModal() {
    this.modalOpen = false;
  }
}
