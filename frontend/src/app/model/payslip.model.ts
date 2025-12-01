// DTO tel qu'il arrive du backend
export interface PayslipDTO {
  id?: number;
  generationDate?: string;
  month?: number;
  year?: number;
  baseSalary?: number;
  bonuses?: number;
  deductions?: number;  // Total des déductions (calculé)
  netPay?: number;
  employeNom?: string;
  customDeductions?: number;
}

// Pour l'affichage / modal
export interface PayslipDisplay {
  id?: number;
  userId?: number;
  employeNom?: string;
  baseSalary?: number;
  bonuses?: number;
  customDeductions?: number;  // ← Déductions personnalisées (modifiable)
  deductions?: number;        // ← Total des déductions (calculé, readonly)
  year?: number;
  month?: number;
  netPay?: number;
}