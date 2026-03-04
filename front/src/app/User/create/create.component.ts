import { Component } from '@angular/core';
import { UserService } from '../../Services/User/user.service';
import { CreateUser } from '../../interfaces/User';
import { Organization } from '../../interfaces/Organization';
import { FormsModule } from '@angular/forms';
import { CommonModule, NgFor, NgIf } from '@angular/common';

@Component({
  selector: 'app-create',
  imports: [FormsModule,NgFor,NgIf,CommonModule],
  templateUrl: './create.component.html',
  styleUrl: './create.component.css'
})
export class CreateComponent {
  userData:CreateUser = {} as CreateUser
  organizations:Organization[] = []
  constructor(private service:UserService){}

  ngOnInit(){
    this.service.getOrganizations().subscribe({
      next:(response)=>{
        this.organizations = response
      },
      error:(err)=>{
        console.log(err);
      }
    })
  }

  createUser(){
    this.service.saveUser(this.userData).subscribe({
      next:(response)=>{},
      error:(err)=>{}
    })
  }



}
