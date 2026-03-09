import { Component } from '@angular/core';
import { ActivityService } from '../../Services/Activity/activity.service';
import { LocationService } from '../../Services/Location/location.service';
import { Activity, CreateActivity, CreateActivityGuest, Proposal, updateActivity } from '../../interfaces/Activity';
import { Room } from '../../interfaces/Location';
import { CongressResponse } from '../../interfaces/Congress';
import { ActivatedRoute } from '@angular/router';
import { CongressService } from '../../Services/Congress/congress.service';
import { ProposalState, ProposalType } from '../../interfaces/Enums';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { User } from '../../interfaces/User';
import { UserService } from '../../Services/User/user.service';
import { ActivityCardComponent } from '../activity-card/activity-card.component';

@Component({
  selector: 'app-create-activity',
  imports: [CommonModule, FormsModule,ActivityCardComponent],
  templateUrl: './create-activity.component.html',
  styleUrl: './create-activity.component.css'
})
export class CreateActivityComponent {
  activityCreate: CreateActivity = {} as CreateActivity
  activityUpdate: updateActivity = {} as updateActivity
  activities: Activity[] = []
  rooms: Room[] = []
  congress: CongressResponse = {} as CongressResponse
  proposals: Proposal[] = []
  editingId: number | null = null;
  users: User[] = []
  activeForm: 'proposal' | 'guest' = 'proposal';
  ProposalType = ProposalType;

  guestCreate: CreateActivityGuest = { users: [] } as any;
  userSearch: string = '';
  constructor(private service: ActivityService, private locationService: LocationService, private route: ActivatedRoute, private congressService: CongressService, private userService: UserService) { }

  ngOnInit() {
    const congressId = parseInt(this.route.snapshot.paramMap.get('id')!);
    this.congressService.getCongressById(congressId).subscribe({
      next: (response) => {
        this.congress = response
        this.locationService.getRoomsByLocationId(this.congress.locationId).subscribe({
          next: (response) => { this.rooms = response }
        })
        this.service.getProposalByCongressIdAndState(congressId, ProposalState.APPROVED).subscribe({
          next: (response) => { this.proposals = response }
        })
        this.userService.getAllUser().subscribe({
          next: (response) => { this.users = response }
        })
      }
    })
    this.service.getActivitiesByCongressId(congressId).subscribe({
      next: (response) => {
        this.activities = response
      }
    })
  }
  get selectedProposal(): Proposal | undefined {
    return this.proposals.find(p => p.id === +this.activityCreate.proposalId);
  }

  crearActividad() {
    this.service.createActivity(this.activityCreate).subscribe({
      next: (response) => {
        this.activities.push(response);
        this.activityCreate = {} as CreateActivity;
        Swal.fire({ title: 'OK', text: 'Actividad creada correctamente', icon: 'success' });
      },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }



  startEdit(activity: Activity) {
    this.editingId = activity.id;
    this.activityUpdate = {
      name: activity.name,
      roomId: activity.roomId,
      capacity: activity.capacity,
      startDate: activity.startDate as any,
      endDate: activity.endDate as any
    };
  }

  cancelEdit() {
    this.editingId = null;
    this.activityUpdate = {} as updateActivity;
  }

  updateActivity(id: number) {
    this.service.updateActivity(this.activityUpdate, id).subscribe({
      next: (response) => {
        const index = this.activities.findIndex(a => a.id === id);
        if (index !== -1) this.activities[index] = response;
        this.cancelEdit();
        Swal.fire({ title: 'OK', text: 'Actividad actualizada', icon: 'success' });
      },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }

  confirmDelete(id: number) {
    Swal.fire({
      title: '¿Eliminar actividad?',
      text: 'Esta acción no se puede deshacer',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then(result => {
      if (result.isConfirmed) {
        this.deleteActivity(id);
      }
    });
  }

  deleteActivity(id: number) {
    this.service.deleteActivity(id).subscribe({
      next: () => {
        this.activities = this.activities.filter(a => a.id !== id);
        Swal.fire({ title: 'OK', text: 'Actividad eliminada', icon: 'success' });
      },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }

  get filteredUsers(): User[] {
    const term = this.userSearch.toLowerCase();
    if (!term) return this.users;
    return this.users.filter(u =>
      u.name.toLowerCase().includes(term) ||
      u.lastName.toLowerCase().includes(term)
    );
  }

  get selectedUsers(): User[] {
    return this.users.filter(u => this.guestCreate.users.includes(u.id));
  }

  toggleUser(userId: number) {
    const index = this.guestCreate.users.indexOf(userId);
    if (index === -1) this.guestCreate.users.push(userId);
    else this.guestCreate.users.splice(index, 1);
  }

  isSelected(userId: number): boolean {
    return this.guestCreate.users.includes(userId);
  }

  crearActividadGuest() {
    this.guestCreate.congressId = this.congress.id;
    this.service.createActivityGuest(this.guestCreate).subscribe({
      next: (response) => {
        this.activities.push(response);
        this.guestCreate = { users: [] } as any;
        this.userSearch = '';
        Swal.fire({ title: 'OK', text: 'Actividad con invitados creada', icon: 'success' });
      },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }
}
