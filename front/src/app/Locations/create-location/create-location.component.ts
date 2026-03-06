import { Component } from '@angular/core';
import { LocationService } from '../../Services/Location/location.service';
import { CreateLocation, Location } from '../../interfaces/Location';
import Swal from 'sweetalert2';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-create-location',
  imports: [FormsModule, CommonModule],
  templateUrl: './create-location.component.html',
  styleUrl: './create-location.component.css'
})
export class CreateLocationComponent {
  locations: Location[] = []
  locationCreate: CreateLocation = {} as Location
  constructor(private service: LocationService, private router: Router) { }
  ngOnInit() {
    this.service.getAllLocations().subscribe({
      next: (response) => { this.locations = response }
    })
  }

  crear() {
    this.service.createLocation(this.locationCreate).subscribe({
      next: () => { Swal.fire({ title: 'OK', text: 'UBICACION CREADO CORRECTAMENTE', icon: 'success' }) },
      error: (err) => { Swal.fire({ title: 'error', text: err.error, icon: 'error' }) }
    })
  }
  goToRooms(id: number) {
    this.router.navigate(['/locationRoom', id]);
  }
}
