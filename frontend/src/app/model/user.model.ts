export interface User {
  id?: number;
  firstName: string;  
  lastName: string;
  fullName?: string; // calculé côté backend
  matricule: string;
  email: string;
  phone?: string;
  address?: string;
  role: string;
  grade?: string;
  department?: string;
  departmentId?: number;
  position?: string;
  positionId?: number;
  contractType?: string;
  baseSalary?: number;
  password?: string;
  hasImage?: boolean;
}