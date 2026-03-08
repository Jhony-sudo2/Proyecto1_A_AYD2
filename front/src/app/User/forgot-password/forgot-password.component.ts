import { Component } from '@angular/core';
import Swal from 'sweetalert2';
import { UserService } from '../../Services/User/user.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-forgot-password',
  imports: [CommonModule,FormsModule],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css'
})
export class ForgotPasswordComponent {
  step: number = 1; // 1 = email, 2 = código + nueva contraseña
  email: string = '';
  code: string = '';
  newPassword: string = '';
  showPassword: boolean = false;

  constructor(private service: UserService, private router: Router) { }

  sendCode() {
    this.service.recoveryPassword(this.email).subscribe({
      next: () => {
        this.step = 2;
        Swal.fire({ title: 'Código enviado', text: 'Revisa tu correo electrónico', icon: 'success' });
      },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }

  confirm() {
    this.service.confirCode(this.email, this.code, this.newPassword).subscribe({
      next: () => {
        Swal.fire({ title: '¡Listo!', text: 'Contraseña actualizada correctamente', icon: 'success' });
        this.router.navigate(['/login']);
      },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }
}
