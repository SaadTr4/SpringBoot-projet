// DTO tel qu'il arrive du backend
export interface PayslipDTO {
  id?: number;
  generationDate?: string;
  month?: number;
  year?: number;
  baseSalary?: number;
  bonuses?: number;
  deductions?: number;
  netPay?: number;
  employeNom?: string;   // le backend doit renvoyer directement le nom
}

// Pour l'affichage / modal
export interface PayslipDisplay {
  id?: number;
  userId?: number;
  employeNom?: string;
  salaireBase?: number;
  prime?: number;
  deduction?: number;
  year?: number;
  month?: number;
}
