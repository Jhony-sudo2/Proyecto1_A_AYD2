import { Component } from '@angular/core';
import { CongressResponse } from '../../interfaces/Congress';
import { CongressService } from '../../Services/Congress/congress.service';
import Swal from 'sweetalert2';
import { CookieService } from 'ngx-cookie-service';
import { AuthService } from '../../Services/Auth/auth.service';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-view-congress',
  imports: [CommonModule,RouterModule],
  templateUrl: './view-congress.component.html',
  styleUrl: './view-congress.component.css'
})
export class ViewCongressComponent {
  congresses: CongressResponse[] = []
  constructor(private service: CongressService, private authService: AuthService,private router:Router) { }
  isLoggedIn: boolean = false
  ngOnInit() {
    this.isLoggedIn = this.authService.isLoggedIn()
    this.service.getAllCongress().subscribe({
      next: (response) => { this.congresses = response },
      error: (err) => { Swal.fire({ title: 'Error', text: err.error, icon: 'error' }) }
    })
  }
  goToDetail(id: number) {
    this.router.navigate(['/congressDetails', id]);
  }

  enroll(congressId: number) {
    this.router.navigate(["inscription",congressId]);
  }
}
