// src/app/model/department.model.ts
export interface Department {
  id?: number;
  name: string;
  code: string;
  description?: string;
  head?: string;      
  nbEmployees?: number; 
}
