import { Component } from '@angular/core';
import { ActivityService } from '../../Services/Activity/activity.service';
import { LocationService } from '../../Services/Location/location.service';
import { Activity, CreateActivity, Proposal } from '../../interfaces/Activity';
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
  activities: Activity[] = []
  rooms: Room[] = []
  congress: CongressResponse = {} as CongressResponse
  proposals: Proposal[] = []
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


  }
