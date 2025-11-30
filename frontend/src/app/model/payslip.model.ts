export interface Payslip {
  id?: number;
  userId: number;
  employeNom?: string;
  salaireBase: number;
  prime: number;
  deduction: number;
  total?: number;
  year: number;
  month: number;
}
