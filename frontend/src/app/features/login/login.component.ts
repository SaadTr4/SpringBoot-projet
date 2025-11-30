import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],  // 👈 OBLIGATOIRE POUR ngForm + *ngIf
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  errorMessage = '';   // 👈 DOIT EXISTER car utilisé dans ton HTML

  constructor(private api: ApiService, private router: Router) {}

  login(form: any) {
    const { username, password } = form;

    this.api.login({ matricule: username, password }).subscribe({
      next: (res: any) => {
        console.log("Connexion OK:", res);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = "Matricule ou mot de passe incorrect";
      }
    });
  }
}
