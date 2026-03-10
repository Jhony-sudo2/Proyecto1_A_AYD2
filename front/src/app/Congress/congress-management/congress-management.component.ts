import { Component } from '@angular/core';
import { CongressResponse } from '../../interfaces/Congress';
import { CongressService } from '../../Services/Congress/congress.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-congress-management',
  imports: [CommonModule],
  templateUrl: './congress-management.component.html',
  styleUrl: './congress-management.component.css'
})
export class CongressManagementComponent {
  congresses: CongressResponse[] = [];

  constructor(private service: CongressService, private router: Router) { }

  ngOnInit() {
    this.service.getAllCongress().subscribe({
      next: (res) => { this.congresses = res; }
    });
  }

  go(path: string, id: number) {
    this.router.navigate([`/${path}`, id]);
  }
}
