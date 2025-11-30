export interface User {
  id?: number;
  firstName: string;  
  lastName: string;
  matricule: string;
  email: string;
  role: string;      
  password?: string;
  get fullName(): string;
}
