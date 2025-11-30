import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { Payslip } from '../../model/payslip.model';
import { User } from '../../model/user.model';

@Component({
  selector: 'app-payslips',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payslips.component.html',
  styleUrls: ['./payslips.component.scss']
})
export class PayslipsComponent implements OnInit {
  payslips: Payslip[] = [];
  employees: User[] = [];
  username = 'Admin'; // à récupérer depuis Auth si nécessaire

  modalOpen = false;
  modalMode: 'add' | 'edit' = 'add';
  selectedPayslip: Payslip = {} as Payslip;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadPayslips();
    this.loadEmployees();
  }

  loadPayslips() {
    this.api.getPayslips().subscribe(res => this.payslips = res);
  }

  loadEmployees() {
    this.api.getUsers().subscribe(res => this.employees = res);
  }

  openModal(mode: 'add' | 'edit', payslip?: Payslip) {
    this.modalMode = mode;
    this.selectedPayslip = payslip ? { ...payslip } : {
      userId: 0,
      salaireBase: 0,
      prime: 0,
      deduction: 0,
      year: new Date().getFullYear(),
      month: new Date().getMonth() + 1
    };
    this.modalOpen = true;
  }

  closeModal() {
    this.modalOpen = false;
    this.selectedPayslip = {} as Payslip;
  }

  savePayslip() {
    if (this.modalMode === 'add') {
      this.api.createPayslip(this.selectedPayslip).subscribe(() => this.loadPayslips());
    } else if (this.modalMode === 'edit' && this.selectedPayslip.id) {
      this.api.updatePayslip(this.selectedPayslip.id, this.selectedPayslip).subscribe(() => this.loadPayslips());
    }
    this.closeModal();
  }

  deletePayslip(p: Payslip) {
    if (confirm(`Voulez-vous supprimer la fiche de paie de ${p.employeNom} ?`)) {
      this.api.deletePayslip(p.id!).subscribe(() => this.loadPayslips());
    }
  }

  filterPayslips(matricule?: string, year?: number, month?: number) {
    const params: any = {};
    if (matricule) params.matricule = matricule;
    if (year) params.year = year;
    if (month) params.month = month;
    this.api.filterPayslips(params).subscribe(res => this.payslips = res);
  }
}
