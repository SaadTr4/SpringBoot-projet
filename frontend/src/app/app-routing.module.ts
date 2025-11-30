import { Routes } from '@angular/router';

import { DashboardComponent } from './features/dashboard/dashboard.component';
import { EmployeesComponent } from './features/employees/employees.component';
import { DepartmentsComponent } from './features/departments/departments.component';
import { ProjectsComponent } from './features/projects/projects.component';
import { PayslipsComponent } from './features/payslips/payslips.component';
import { LoginComponent } from './features/login/login.component';
import { PositionsComponent } from './features/positions/positions.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'employees', component: EmployeesComponent },
  { path: 'departments', component: DepartmentsComponent },
  { path: 'projects', component: ProjectsComponent },
  { path: 'payslips', component: PayslipsComponent },
  { path: 'positions', component: PositionsComponent }
];

