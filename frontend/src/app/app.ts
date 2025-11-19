// app.ts
import { Component, signal } from '@angular/core';
import { ProjectComponent } from './project/project.component';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrls: ['./app.scss'],
  standalone: true,
  imports: [RouterModule], // si tu veux l’utiliser directement dans le template
})
export class App {
  protected readonly title = signal('frontend');
}
