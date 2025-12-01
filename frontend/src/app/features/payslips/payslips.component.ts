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

  months = [
  'Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin',
  'Juillet', 'Août', 'Septembre', 'Octobre', 'Novembre', 'Décembre' 
  ];

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadPayslips();
    this.loadEmployees();
  }

  loadPayslips() {
    this.api.getPayslips().subscribe({
      next: (res: PayslipDisplay[]) => {
        console.log('Payslips reçus:', res);
        this.payslips = res;
      },
      error: (err) => console.error('Erreur chargement payslips:', err)
    });
  }
  loadEmployees() {
    this.api.getUsers().subscribe(res => this.employees = res);
  }


openModal(mode: 'add' | 'edit', p?: any) {
  this.modalMode = mode;
  this.modalOpen = true;

  if (mode === 'edit' && p) {
    this.selectedPayslip = {
      id: p.id,
      employeNom: p.employeNom,
      baseSalary: p.baseSalary,
      bonuses: p.bonuses,
      customDeductions: p.customDeductions,   // ← OBLIGATOIRE
      deductions: p.deductions,
      netPay: p.netPay,
      year: p.year,
      month: p.month,
      userId: p.userId
    };

    console.log("Modal EDIT chargé avec :", this.selectedPayslip);
  } 
  else {
    this.selectedPayslip = {
      bonuses: 0,
      customDeductions: 0,
      year: new Date().getFullYear(),
      month: new Date().getMonth() + 1
    };
  }
}




  closeModal() {
    this.modalOpen = false;
    this.selectedPayslip = {} as PayslipDisplay;
  }

savePayslip() {
  console.log('savePayslip appelé', this.modalMode, this.selectedPayslip); // ← Ajout pour déboguer
  
  if (this.modalMode === 'add') {
    if (!this.selectedPayslip.userId) {
      alert('Veuillez sélectionner un employé');
      return;
    }
    this.api.createPayslip(this.selectedPayslip).subscribe({
      next: () => {
        this.loadPayslips();
        this.closeModal();
      },
      error: (err) => {
        console.error('Erreur création:', err); // ← Ajout pour déboguer
        alert('Erreur: ' + (err.error?.message || err.message));
      }
    });
  } else if (this.modalMode === 'edit' && this.selectedPayslip.id) {
    this.api.updatePayslip(this.selectedPayslip.id, this.selectedPayslip).subscribe({
      next: () => {
        this.loadPayslips();
        this.closeModal();
      },
      error: (err) => {
        console.error('Erreur modification:', err); // ← Ajout pour déboguer
        alert('Erreur: ' + (err.error?.message || err.message));
      }
    });
  }
}

  exportPDF(payslip: PayslipDisplay) {
    if (!payslip.id) return;
    
    this.api.exportPDF(payslip.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `Fiche_${payslip.employeNom}_${payslip.month}_${payslip.year}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => console.error('Erreur export PDF:', err)
    });
  }


  deletePayslip(p: PayslipDisplay) {
    if (confirm(`Voulez-vous supprimer la fiche de paie de ${p.employeNom} ?`)) {
      this.api.deletePayslip(p.id!).subscribe(() => this.loadPayslips());
    }
  }
filterPayslips(matricule?: string, year?: number, month?: number) {
  const params: Record<string, string | number> = {};
  if (matricule) params['matricule'] = matricule.trim();
  if (year) params['year'] = year;
  if (month) params['month'] = month;

this.api.filterPayslips(params).subscribe(res => {
  this.payslips = res; // DTO déjà prêt
});
}


  // Calcul total à afficher côté Angular
  getTotal(p: PayslipDisplay) {
    return (p.baseSalary || 0) + (p.bonuses || 0) - (p.deductions || 0);
  }
}
