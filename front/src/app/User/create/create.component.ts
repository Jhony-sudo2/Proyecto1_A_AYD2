import { Component } from '@angular/core';
import { UserService } from '../../Services/User/user.service';
import { Organization } from '../../interfaces/Organization';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { CreateUser, CreateUserWithRol, Rol } from '../../interfaces/User';
import { AuthService } from '../../Services/Auth/auth.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-create',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './create.component.html',
  styleUrl: './create.component.css'
})
export class CreateComponent {
  organizations: Organization[] = [];
  rols: Rol[] = []
  userCreate: CreateUserWithRol = {} as CreateUserWithRol;
  rol: number = 0
  showPassword: boolean = false;

  constructor(private service: UserService, private authService: AuthService) {

  }

  ngOnInit() {
    this.rol = this.authService.getRol()
    console.log("rol:",this.rol);
    
    this.service.getAllRols().subscribe({
      next: (response) => { this.rols = response }
    })
    this.service.getOrganizations().subscribe({
      next: (response) => { this.organizations = response }
    })
  }

  onImageSelected(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => { this.userCreate.imageUrl = reader.result as string; };
    reader.readAsDataURL(file);
  }

  createUserNormal() {
    const { rol, ...data } = this.userCreate;
    this.authService.registerUser(data as CreateUser).subscribe({
      next: () => Swal.fire({ title: '¡Cuenta creada!', text: 'Ya puedes iniciar sesión', icon: 'success' }),
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }

  createUserWithRol() {
    this.service.saveUser(this.userCreate).subscribe({
      next: () => {
        Swal.fire({ title: 'OK', text: 'Usuario creado correctamente', icon: 'success' });
        this.userCreate = {} as CreateUserWithRol;
      },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }
}
