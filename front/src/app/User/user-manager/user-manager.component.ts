import { Component } from '@angular/core';
import { UserService } from '../../Services/User/user.service';
import { User } from '../../interfaces/User';
import Swal from 'sweetalert2';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-user-manager',
  imports: [CommonModule,FormsModule],
  templateUrl: './user-manager.component.html',
  styleUrl: './user-manager.component.css'
})
export class UserManagerComponent {
  users: User[] = []
  searchTerm: string = '';
  activeFilter: string = 'all';

  filters = [
    { label: 'Todos', value: 'all' },
    { label: 'Activos', value: 'active' },
    { label: 'Inactivos', value: 'inactive' }
  ];
  constructor(private service: UserService) { }
  ngOnInit() {
    this.service.getAllUser().subscribe({
      next: (response) => { this.users = response }
    })
  }

  get filteredUsers(): User[] {
    return this.users.filter(u => {
      const matchFilter =
        this.activeFilter === 'all' ||
        (this.activeFilter === 'active' && u.active) ||
        (this.activeFilter === 'inactive' && !u.active);

      const term = this.searchTerm.toLowerCase();
      const matchSearch = !term ||
        u.name.toLowerCase().includes(term) ||
        u.lastName.toLowerCase().includes(term) ||
        u.email.toLowerCase().includes(term) ||
        u.identification.toLowerCase().includes(term);

      return matchFilter && matchSearch;
    });
  }

  countByFilter(filter: string): number {
    if (filter === 'all') return this.users.length;
    if (filter === 'active') return this.users.filter(u => u.active).length;
    if (filter === 'inactive') return this.users.filter(u => !u.active).length;
    return 0;
  }

  confirmChangeState(user: User) {
    Swal.fire({
      title: user.active ? '¿Desactivar usuario?' : '¿Activar usuario?',
      text: `${user.name} ${user.lastName} quedará ${user.active ? 'inactivo' : 'activo'}`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, confirmar',
      cancelButtonText: 'Cancelar'
    }).then(result => {
      if (result.isConfirmed) {
        this.changeState(user.id);
      }
    });
  }

  changeState(userId: number) {
    this.service.changeState(userId).subscribe({
      next: () => {
        const user = this.users.find(u => u.id === userId);
        if (user) user.active = !user.active; 
        Swal.fire({ title: 'OK', text: 'Estado actualizado', icon: 'success' });
      },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }
}
