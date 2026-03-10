import { Component } from '@angular/core';
import { NavbarComponent } from './nav-bar/nav-bar.component';

@Component({
  selector: 'app-root',
  imports: [NavbarComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {}
