import { Component } from '@angular/core';
import { CongressService } from '../../Services/Congress/congress.service';
import { CreateCongress } from '../../interfaces/Congress';
import { CookieService } from 'ngx-cookie-service';
import Swal from 'sweetalert2';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Location } from '../../interfaces/Location';

@Component({
  selector: 'app-create-congress',
  imports: [CommonModule, FormsModule],
  templateUrl: './create-congress.component.html',
  styleUrl: './create-congress.component.css'
})
export class CreateCongressComponent {
  newCongress: CreateCongress = {} as CreateCongress;
  locations: Location[] = []
  organizationId: number = 0;
  constructor(private service: CongressService, private cookieService: CookieService) { }

  ngOnInit() {
    const token = this.cookieService.get('access_token');
    const payload = JSON.parse(atob(token.split('.')[1]));
    this.organizationId = payload.organizationId;
    this.service.getLocations().subscribe((res: any) => {
      this.locations = res;
    });
  }
  createCongress() {
    this.newCongress.organizationId = this.organizationId;
    this.service.createCongress(this.newCongress).subscribe({
      next: () => Swal.fire({ title: 'Éxito', text: 'Congreso creado', icon: 'success' }),
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }
  onImageSelected(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = () => {
      this.newCongress.imageUrl = reader.result as string;
    };
    reader.readAsDataURL(file);
  }
}
