import { Component, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../layout/sidebar/sidebar.component';
import { HeaderComponent } from '../../layout/header/header.component';

import Chart from 'chart.js/auto';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements AfterViewInit {

  employeesCount = 120;
  projectsCount = 15;
  departmentsCount = 6;

  ngAfterViewInit() {
    this.renderCharts();
  }

  renderCharts() {

    // Employés par rôle
    new Chart("chartEmployees", {
      type: 'doughnut',
      data: {
        labels: ['Employé', 'Administrateur', 'Chef Département', 'Chef Projet'],
        datasets: [{
          data: [85, 5, 3, 7],
          backgroundColor: ['#60a5fa','#f59e0b','#a78bfa','#34d399'],
          borderColor: 'rgba(255,255,255,.65)',
          borderWidth: 2
        }]
      },
      options: { responsive: true, maintainAspectRatio: false }
    });

    // Départements
    new Chart("chartDepartments", {
      type: 'bar',
      data: {
        labels: ['RH','Finance','Info','Marketing','Prod','R&D'],
        datasets: [{
          label: 'Effectif',
          data: [12,18,34,15,25,16],
          backgroundColor: ['#22d3ee','#f97316','#84cc16','#e879f9','#f43f5e','#10b981']
        }]
      },
      options: { responsive: true, maintainAspectRatio: false }
    });

    // Statut projets
    new Chart("chartProjects", {
      type: 'pie',
      data: {
        labels: ['En cours', 'Terminés', 'Annulés'],
        datasets: [{
          data: [9,4,2],
          backgroundColor: ['#3b82f6','#10b981','#ef4444']
        }]
      },
      options: { responsive: true, maintainAspectRatio: false }
    });

  }
}
