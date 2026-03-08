import { Component } from '@angular/core';
import { ActivityService } from '../../Services/Activity/activity.service';
import { LocationService } from '../../Services/Location/location.service';
import { Activity, CreateActivity, Proposal, updateActivity } from '../../interfaces/Activity';
import { Room } from '../../interfaces/Location';
import { CongressResponse } from '../../interfaces/Congress';
import { ActivatedRoute } from '@angular/router';
import { CongressService } from '../../Services/Congress/congress.service';
import { ProposalState } from '../../interfaces/Enums';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-create-activity',
  imports: [CommonModule, FormsModule],
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
  constructor(private service: ActivityService, private locationService: LocationService, private route: ActivatedRoute, private congressService: CongressService) { }

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


}
