import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../model/user.model';
import { Project } from '../model/project.model';
import { Department } from '../model/department.model';
import { Position } from '../model/position.model';
import { PayslipDTO , PayslipDisplay} from '../model/payslip.model';
import { map } from 'rxjs/operators';


@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  private getHttpOptions() {
    return {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
      withCredentials: true
    };
  }

  // ===== Auth =====
  login(credentials: { matricule: string, password: string }): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/login`, credentials, this.getHttpOptions());
  }

  logout(): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/logout`, {}, this.getHttpOptions());
  }

  checkAuth(): Observable<any> {
    return this.http.get(`${this.baseUrl}/auth/check`, this.getHttpOptions());
  }

  // ===== Utilisateurs =====
  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.baseUrl}/users`, this.getHttpOptions());
  }

  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/users/${id}`, this.getHttpOptions());
  }

  createUser(user: User): Observable<User> {
    return this.http.post<User>(`${this.baseUrl}/users`, user, this.getHttpOptions());
  }

  getUserImageUrl(userId: number): string {
  // Ajoute un cache-buster pour forcer le reload
  return `http://localhost:8080/api/users/${userId}/image?t=${new Date().getTime()}`;
}


  updateUser(id: number, user: Partial<User>): Observable<User> {
    return this.http.post<User>(`${this.baseUrl}/users/${id}`, user, this.getHttpOptions());
  }

  deleteUser(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/users/${id}`, this.getHttpOptions());
  }

  searchUsers(params: any): Observable<User[]> {
    return this.http.get<User[]>(`${this.baseUrl}/users/search`, {
      ...this.getHttpOptions(),
      params
    });
  }
    // ===== Projets =====
  getProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.baseUrl}/projects`, this.getHttpOptions());
  }

  getProjectById(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.baseUrl}/projects/${id}`, this.getHttpOptions());
  }

  createProject(project: Project): Observable<Project> {
    return this.http.post<Project>(`${this.baseUrl}/projects`, project, this.getHttpOptions());
  }

  updateProjectManager(id: number, managerId: number): Observable<boolean> {
    return this.http.put<boolean>(`${this.baseUrl}/projects/${id}/manager/${managerId}`, {}, this.getHttpOptions());
  }

  assignUser(projectId: number, userId: number): Observable<boolean> {
    return this.http.put<boolean>(`${this.baseUrl}/projects/${projectId}/assign/${userId}`, {}, this.getHttpOptions());
  }

  removeUser(projectId: number, userId: number): Observable<boolean> {
    return this.http.put<boolean>(`${this.baseUrl}/projects/${projectId}/remove/${userId}`, {}, this.getHttpOptions());
  }

  updateStatus(projectId: number, status: string): Observable<boolean> {
    return this.http.put<boolean>(`${this.baseUrl}/projects/${projectId}/status?status=${status}`, {}, this.getHttpOptions());
  }

  deleteProject(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/projects/${id}`, this.getHttpOptions());
  }

  filterProjects(params: any): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.baseUrl}/projects/filter`, { ...this.getHttpOptions(), params });
  }

  // ===== Départements =====
getDepartments(): Observable<Department[]> {
  return this.http.get<Department[]>(`${this.baseUrl}/departments`, this.getHttpOptions());
}

getDepartmentById(id: number): Observable<Department> {
  return this.http.get<Department>(`${this.baseUrl}/departments/${id}`, this.getHttpOptions());
}

createDepartment(dept: Partial<Department>): Observable<Department> {
  return this.http.post<Department>(`${this.baseUrl}/departments`, dept, this.getHttpOptions());
}

updateDepartment(id: number, dept: Partial<Department>): Observable<Department> {
  return this.http.put<Department>(`${this.baseUrl}/departments/${id}`, dept, this.getHttpOptions());
}

deleteDepartment(id: number): Observable<any> {
  return this.http.delete(`${this.baseUrl}/departments/${id}`, this.getHttpOptions());
}

// Assign / Remove users
assignUserToDepartment(departmentId: number, matricule: string): Observable<any> {
  return this.http.post(`${this.baseUrl}/departments/${departmentId}/assign?matricule=${matricule}`, {}, this.getHttpOptions());
}

removeUserFromDepartment(departmentId: number, matricule: string): Observable<any> {
  return this.http.post(`${this.baseUrl}/departments/${departmentId}/remove?matricule=${matricule}`, {}, this.getHttpOptions());
}

// ===== Positions =====
getPositions(): Observable<Position[]> {
  return this.http.get<Position[]>(`${this.baseUrl}/positions`, this.getHttpOptions());
}

getPositionById(id: number): Observable<Position> {
  return this.http.get<Position>(`${this.baseUrl}/positions/${id}`, this.getHttpOptions());
}

createPosition(position: Position): Observable<Position> {
  return this.http.post<Position>(`${this.baseUrl}/positions`, position, this.getHttpOptions());
}

updatePosition(id: number, position: Position): Observable<Position> {
  return this.http.put<Position>(`${this.baseUrl}/positions/${id}`, position, this.getHttpOptions());
}

deletePosition(id: number): Observable<any> {
  return this.http.delete(`${this.baseUrl}/positions/${id}`, this.getHttpOptions());
}

// Users liés à un poste
getUsersOfPosition(id: number): Observable<User[]> {
  return this.http.get<User[]>(`${this.baseUrl}/positions/${id}/users`, this.getHttpOptions());
}

countUsersOfPosition(id: number): Observable<number> {
  return this.http.get<number>(`${this.baseUrl}/positions/${id}/count`, this.getHttpOptions());
}

assignUserToPosition(id: number, matricule: string): Observable<any> {
  return this.http.post(`${this.baseUrl}/positions/${id}/assign?matricule=${matricule}`, {}, this.getHttpOptions());
}

removeUserFromPosition(id: number, matricule: string): Observable<any> {
  return this.http.post(`${this.baseUrl}/positions/${id}/remove?matricule=${matricule}`, {}, this.getHttpOptions());
}

 // ===== Fiches de paie =====
getPayslips(): Observable<PayslipDisplay[]> {
  return this.http.get<PayslipDTO[]>(`${this.baseUrl}/payslips`, this.getHttpOptions()).pipe(
    map(res => res.map(p => ({
      id: p.id,
      employeNom: p.employeNom ?? '',
      salaireBase: p.baseSalary ?? 0,
      prime: p.bonuses ?? 0,
      deduction: p.deductions ?? 0,
      year: p.year,
      month: p.month
    })))
  );
}

getPayslipsByUser(userId: number): Observable<PayslipDisplay[]> {
  return this.http.get<PayslipDTO[]>(`${this.baseUrl}/payslips/user/${userId}`, this.getHttpOptions()).pipe(
    map(res => res.map(p => ({
      id: p.id,
      employeNom: p.employeNom ?? '',
      salaireBase: p.baseSalary ?? 0,
      prime: p.bonuses ?? 0,
      deduction: p.deductions ?? 0,
      year: p.year,
      month: p.month
    })))
  );
}

filterPayslips(params: any): Observable<PayslipDisplay[]> {
  return this.http.get<PayslipDTO[]>(`${this.baseUrl}/payslips/filter`, { ...this.getHttpOptions(), params }).pipe(
    map(res => res.map(p => ({
      id: p.id,
      employeNom: p.employeNom ?? '',
      salaireBase: p.baseSalary ?? 0,
      prime: p.bonuses ?? 0,
      deduction: p.deductions ?? 0,
      year: p.year,
      month: p.month
    })))
  );
}

createPayslip(payslip: PayslipDisplay): Observable<PayslipDisplay> {
  const params: Record<string, string | number> = {
    matricule: payslip.userId!,
    year: payslip.year!,
    month: payslip.month!,
    bonuses: payslip.prime ?? 0,
    deductions: payslip.deduction ?? 0
  };

  return this.http.post<PayslipDTO>(`${this.baseUrl}/payslips/create`, null, { ...this.getHttpOptions(), params }).pipe(
    map(p => ({
      id: p.id,
      employeNom: p.employeNom ?? '',
      salaireBase: p.baseSalary ?? 0,
      prime: p.bonuses ?? 0,
      deduction: p.deductions ?? 0,
      year: p.year,
      month: p.month,
      userId: payslip.userId
    }))
  );
}

updatePayslip(id: number, payslip: PayslipDisplay): Observable<PayslipDisplay> {
  const body = {
    bonuses: payslip.prime ?? 0,
    deductions: payslip.deduction ?? 0
  };

  return this.http.put<PayslipDTO>(`${this.baseUrl}/payslips/${id}`, body, this.getHttpOptions()).pipe(
    map(p => ({
      id: p.id,
      employeNom: p.employeNom ?? '',
      salaireBase: p.baseSalary ?? 0,
      prime: p.bonuses ?? 0,
      deduction: p.deductions ?? 0,
      year: p.year,
      month: p.month,
      userId: payslip.userId
    }))
  );
}

deletePayslip(id: number): Observable<any> {
  return this.http.delete(`${this.baseUrl}/payslips/${id}`, this.getHttpOptions());
}
}
