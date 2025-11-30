import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';  //  AJOUT CRUCIAL
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-login',
  standalone: true,  //  Composant standalone
  imports: [
    CommonModule,    //  Pour *ngIf
    FormsModule      //  Pour ngForm, ngModel
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  errorMessage: string = '';

  constructor(
    private apiService: ApiService,
    private router: Router
  ) {}

  login(formValue: { matricule: string, password: string }) {
    console.log(' Tentative de connexion avec:', formValue);

    // Validation
    if (!formValue.matricule || !formValue.password) {
      this.errorMessage = 'Veuillez remplir tous les champs';
      return;
    }

    // Appel API
    this.apiService.login(formValue).subscribe({
      next: (response) => {
        console.log(' Connexion réussie:', response);
        this.errorMessage = '';
        // Redirection vers le dashboard
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        console.error(' Erreur de connexion:', error);

        if (error.status === 401) {
          this.errorMessage = 'Matricule ou mot de passe incorrect';
        } else if (error.status === 0) {
          this.errorMessage = 'Impossible de contacter le serveur';
        } else {
          this.errorMessage = 'Erreur de connexion au serveur';
        }
      }
    });
  }
}
