import { Component } from '@angular/core';
import { CongressService } from '../../Services/Congress/congress.service';
import { CongressResponse } from '../../interfaces/Congress';
import { Activity } from '../../interfaces/Activity';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ActivityCardComponent } from '../../Activities/activity-card/activity-card.component';
import { AuthService } from '../../Services/Auth/auth.service';

@Component({
  selector: 'app-detail-congress',
  imports: [CommonModule, ActivityCardComponent],
  templateUrl: './detail-congress.component.html',
  styleUrl: './detail-congress.component.css'
})
export class DetailCongressComponent {
  congressId: number = 0
  congress: CongressResponse = {} as CongressResponse
  activities: Activity[] = []
  isLoggedIn: boolean = false
  constructor(private servicio: CongressService, private route: ActivatedRoute,private authService:AuthService) { }
  ngOnInit() {
    this.isLoggedIn = this.authService.isLoggedIn()
    this.congressId = parseInt(this.route.snapshot.paramMap.get('id')!)
    this.servicio.getCongressById(this.congressId).subscribe({
      next: (response) => { this.congress = response }
    })
    this.servicio.getActivitiesByCongressId(this.congressId).subscribe({
      next: (response) => { this.activities = response }
    })
  }
  enrollWorkshop(activityId: number) {
    console.log('Inscribirse al taller:', activityId);
  }
}
