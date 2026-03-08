import { Component } from '@angular/core';
import { Activity } from '../../interfaces/Activity';
import { AttendanceService } from '../../Services/Attendance/attendance.service';
import { ActivityService } from '../../Services/Activity/activity.service';
import { ActivatedRoute } from '@angular/router';
import { Atteendance, CreateAtteendance } from '../../interfaces/Atteendance';
import Swal from 'sweetalert2';
import { CongressResponse } from '../../interfaces/Congress';
import { CongressService } from '../../Services/Congress/congress.service';
import { AttendanceType } from '../../interfaces/Enums';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-attendance',
  imports: [FormsModule, CommonModule],
  templateUrl: './attendance.component.html',
  styleUrl: './attendance.component.css'
})
export class AttendanceComponent {
  activities: Activity[] = []
  newAteendance: CreateAtteendance = {} as CreateAtteendance
  atteendancesList: Atteendance[] = []
  congress: CongressResponse = {} as CongressResponse
  selectedActivity: Activity | null = null;

  constructor(private service: AttendanceService, private activityService: ActivityService, private route: ActivatedRoute, private congressService: CongressService) { }

  ngOnInit() {
    const id = parseInt(this.route.snapshot.paramMap.get('id')!)
    this.congressService.getCongressById(id).subscribe({
      next: (response) => { this.congress = response }
    })
    this.activityService.getActivitiesByCongressId(id).subscribe({
      next: (response) => { this.activities = response }
    })
  }


  get capacityPercent(): number {
    if (!this.selectedActivity || this.isUnlimited) return 0;
    return Math.min((this.atteendancesList.length / this.selectedActivity.capacity) * 100, 100);
  }

  selectActivity(activity: Activity) {
    this.selectedActivity = activity;
    this.newAteendance = {
      activityId: activity.id,
      userIdentification: '',
      date: '',
      type: AttendanceType.ATTENDACE  // siempre ATTENDACE
    };
    this.getAtteendancesByActivityId(activity.id);
  }

  saveAtteendance() {
    this.service.create(this.newAteendance).subscribe({
      next: () => {
        Swal.fire({ title: 'OK', text: 'Asistencia registrada', icon: 'success' });
        this.newAteendance.userIdentification = '';
        this.newAteendance.date = '';
        if (this.selectedActivity) this.getAtteendancesByActivityId(this.selectedActivity.id);
      },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }

  getAtteendancesByActivityId(id: number) {
    this.service.getAttendancesByActivity(id).subscribe({
      next: (response) => { this.atteendancesList = response; }
    });
  }
  get isUnlimited(): boolean {
    return this.selectedActivity?.type === 'CONFERENCE';
  }

  
}
