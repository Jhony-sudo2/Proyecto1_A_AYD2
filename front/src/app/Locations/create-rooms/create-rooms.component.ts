import { Component } from '@angular/core';
import { LocationService } from '../../Services/Location/location.service';
import { CreateRoom, Location, Room } from '../../interfaces/Location';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-create-rooms',
  imports: [CommonModule, FormsModule],
  templateUrl: './create-rooms.component.html',
  styleUrl: './create-rooms.component.css'
})
export class CreateRoomsComponent {
  location: Location = {} as Location
  rooms: Room[] = []
  roomCreate: CreateRoom = {} as Room
  editingId: number | null = null;

  constructor(private service: LocationService, private route: ActivatedRoute) { }
  ngOnInit() {
    const locationId = parseInt(this.route.snapshot.paramMap.get('id')!)
    this.service.getLocationById(locationId).subscribe({
      next: (response) => { this.location = response }
    })
    this.service.getRoomsByLocationId(locationId).subscribe({
      next: (response) => { this.rooms = response }
    })
  }
  startEdit(room: Room) {
    this.editingId = room.id;
    this.roomCreate = { name: room.name, capacity: room.capacity, equipment: room.equipment, description: room.description };
  }

  cancelEdit() {
    this.editingId = null;
    this.roomCreate = {} as CreateRoom;
  }

  crearRoom() {
    this.service.createRomm(this.roomCreate, this.location.id).subscribe({
      next: () => {
        Swal.fire({ title: 'OK', text: 'Sala creada correctamente', icon: 'success' });
        this.roomCreate = {} as CreateRoom;
        this.service.getRoomsByLocationId(this.location.id).subscribe({ next: (r) => this.rooms = r });
      },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }

  updateRoom(id: number) {
    this.service.updateRoom(this.roomCreate, id).subscribe({
      next: () => {
        Swal.fire({ title: 'OK', text: 'Sala actualizada', icon: 'success' });
        this.cancelEdit();
        this.service.getRoomsByLocationId(this.location.id).subscribe({ next: (r) => this.rooms = r });
      },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }

  confirmDelete(id: number) {
    Swal.fire({
      title: '¿Eliminar sala?',
      text: 'Esta acción no se puede deshacer',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.service.deleteRoom(id).subscribe({
          next: () => {
            this.rooms = this.rooms.filter(r => r.id !== id);
            Swal.fire({ title: 'OK', text: 'Sala eliminada', icon: 'success' });
          },
          error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
        });
      }
    });
  }
}
