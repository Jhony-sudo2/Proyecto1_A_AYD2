import { Component } from '@angular/core';
import { AuthService } from '../../Services/Auth/auth.service';
import { Login } from '../../interfaces/Auth';
import Swal from 'sweetalert2';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  imports: [FormsModule,CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  data:Login = {} as Login
  constructor(private service:AuthService){}

  login(){
    this.service.login(this.data).subscribe({
      next:(response)=>{Swal.fire({title:'OK',text:'Inicio de sesion correct',icon:'success'})},
      error:(err)=>{Swal.fire({title:'error',text:err.error,icon:'error'})}
    })
  }

}
