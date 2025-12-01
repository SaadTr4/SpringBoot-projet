import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { PayslipDTO, PayslipDisplay } from '../../model/payslip.model';
import { User } from '../../model/user.model';

@Component({
  selector: 'app-payslips',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payslips.component.html',
  styleUrls: ['./payslips.component.scss']
})
export class PayslipsComponent implements OnInit {
  payslips: PayslipDisplay[] = [];
  employees: User[] = [];
  username = 'Admin'; // à remplacer par le nom réel si nécessaire

  modalOpen = false;
  modalMode: 'add' | 'edit' = 'add';
  selectedPayslip: PayslipDisplay = {} as PayslipDisplay;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadPayslips();
    this.loadEmployees();
  }

loadPayslips() {
this.api.getPayslips().subscribe((res: PayslipDTO[]) => {
  console.log('Payslips reçus:', res);
  this.payslips = res.map(p => ({
    id: p.id,
    employeNom: p.employeNom || '',
    salaireBase: p.baseSalary ?? 0,
    prime: p.bonuses ?? 0,
    deduction: p.deductions ?? 0,
    year: p.year,
    month: p.month
  }));
});

}

  loadEmployees() {
    this.api.getUsers().subscribe(res => this.employees = res);
  }


openModal(mode: 'add' | 'edit', payslip?: PayslipDisplay) {
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
    this.selectedPayslip = {} as PayslipDisplay;
  }

  savePayslip() {
    if (this.modalMode === 'add') {
      this.api.createPayslip(this.selectedPayslip).subscribe(() => this.loadPayslips());
    } else if (this.modalMode === 'edit' && this.selectedPayslip.id) {
      this.api.updatePayslip(this.selectedPayslip.id, this.selectedPayslip).subscribe(() => this.loadPayslips());
    }
    this.closeModal();
  }

  deletePayslip(p: PayslipDisplay) {
    if (confirm(`Voulez-vous supprimer la fiche de paie de ${p.employeNom} ?`)) {
      this.api.deletePayslip(p.id!).subscribe(() => this.loadPayslips());
    }
  }
filterPayslips(matricule?: string, year?: number, month?: number) {
  const params: Record<string, string | number> = {};
  if (matricule) params['matricule'] = matricule;
  if (year) params['year'] = year;
  if (month) params['month'] = month;

this.api.filterPayslips(params).subscribe(res => {
  this.payslips = res; // DTO déjà prêt
});
}


  // Calcul total à afficher côté Angular
  getTotal(p: PayslipDisplay) {
    return (p.salaireBase || 0) + (p.prime || 0) - (p.deduction || 0);
  }
}
