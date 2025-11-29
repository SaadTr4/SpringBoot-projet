import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';




@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  errorMessage = '';

  constructor(private router: Router) {}

  login(form: any) {
    const username = form.username;
    const password = form.password;

    // ⚠️ TEMPORAIRE (backend non prêt)
    if (username === 'admin' && password === 'admin') {
      this.router.navigate(['/dashboard']);
    } else {
      this.errorMessage = 'Nom d’utilisateur ou mot de passe incorrect.';
    }
  }
}
