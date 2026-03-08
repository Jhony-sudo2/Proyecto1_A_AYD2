import { Component } from '@angular/core';
import { CongressService } from '../../Services/Congress/congress.service';
import { UserService } from '../../Services/User/user.service';
import { User } from '../../interfaces/User';
import { ActivatedRoute } from '@angular/router';
import Swal from 'sweetalert2';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-scientific-committee',
  imports: [CommonModule,FormsModule],
  templateUrl: './scientific-committee.component.html',
  styleUrl: './scientific-committee.component.css'
})
export class ScientificCommitteeComponent {
  users: User[] = []
  usersCommittee: User[] = []
  congressId: number = 0
  userId: number = 0
  searchTerm: string = '';
  selectedUserId: number | null = null;

  constructor(private service: CongressService, private userService: UserService, private route: ActivatedRoute) { }

  ngOnInit() {
    this.congressId = parseInt(this.route.snapshot.paramMap.get('id')!);
    this.userService.getAllUser().subscribe({
      next: (response) => { this.users = response; }
    });
    this.get();
  }
  
  get availableUsers(): User[] {
    const committeeIds = new Set(this.usersCommittee.map(u => u.id));
    return this.users.filter(u => !committeeIds.has(u.id));
  }

  get filteredAvailableUsers(): User[] {
    const term = this.searchTerm.toLowerCase();
    if (!term) return this.availableUsers;
    return this.availableUsers.filter(u =>
      u.name.toLowerCase().includes(term) ||
      u.lastName.toLowerCase().includes(term) ||
      u.email.toLowerCase().includes(term)
    );
  }

  selectUser(user: User) {
    this.selectedUserId = user.id;
    this.userId = user.id;
  }


  save() {
    this.service.saveCommite(this.congressId, this.userId).subscribe({
      next: () => {
        Swal.fire({ title: 'OK', text: 'Miembro agregado al comité', icon: 'success' });
        this.selectedUserId = null;
        this.get();
      },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }

  confirmDelete(user: User) {
    Swal.fire({
      title: '¿Eliminar del comité?',
      text: `${user.name} ${user.lastName} será removido`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then(result => {
      if (result.isConfirmed) {
        this.userId = user.id;
        this.delete();
      }
    });
  }

  delete() {
    this.service.deleteCommite(this.congressId, this.userId).subscribe({
      next: () => {
        Swal.fire({ title: 'OK', text: 'Miembro eliminado del comité', icon: 'success' });
        this.get();
      },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }

  get() {
    this.service.getCommite(this.congressId).subscribe({
      next: (response) => { this.usersCommittee = response; }
    });
  }
}
