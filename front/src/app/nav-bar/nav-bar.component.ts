import { Component } from '@angular/core';
import { AuthService } from '../Services/Auth/auth.service';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
interface NavItem {
  label: string;
  route: string;
  icon: string;
  roles: number[]; // roles que pueden verlo, [] = todos incluso sin login
}
@Component({
  selector: 'app-nav-bar',
  imports: [CommonModule,RouterModule],
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.css'
  
})


export class NavbarComponent {
  collapsed = false;
  rol: number = 0;
  isLoggedIn: boolean = false;

  navItems: NavItem[] = [
    // Públicas
    { label: 'Congresos', route: '/congress', icon: 'congress', roles: [] },
    { label: 'Mi perfil', route: '/profile', icon: 'profile', roles: [1, 2, 3, 4] },
    { label: 'Registro', route: '/auth/register', icon: 'register', roles: [] },
    { route: '/myCertificates', label: 'Mis congresos', icon: 'certificate', roles: [1, 2, 3, 4] },

    // Normal
    { label: 'Proponer trabajo', route: '/activity/proposalCreate', icon: 'proposal', roles: [3] },

    // Admin sistema
    { label: 'Usuarios', route: '/userManager', icon: 'users', roles: [1] },
    { label: 'Organizaciones', route: '/organizations', icon: 'org', roles: [1] },
    { label: 'Configuración', route: '/sysconfiguration', icon: 'config', roles: [1] },
    { label: 'Reportes', route: '/reports', icon: 'reports', roles: [1] },
    { label: 'Crear congreso', route: '/congress/create', icon: 'newcongress', roles: [1] },

    // Admin congreso
    { label: 'Gestión congresos', route: '/congressManagement', icon: 'management', roles: [1,2] },
    { label: 'Reportes', route: '/reports', icon: 'reports', roles: [2] },
    { label: 'Ubicaciones', route: '/Locations', icon: 'location', roles: [1, 2] },
  ];

  constructor(public authService: AuthService, public router: Router) { }

  ngOnInit() {
    this.refresh();
  }

  refresh() {
    this.isLoggedIn = this.authService.isLoggedIn();
    this.rol = this.authService.getRol();
  }

  get visibleItems(): NavItem[] {
    return this.navItems.filter(item => {
      if (item.roles.length === 0) return true;           // público
      if (!this.isLoggedIn) return false;
      return item.roles.includes(this.rol);
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  toggle() {
    this.collapsed = !this.collapsed;
  }
}