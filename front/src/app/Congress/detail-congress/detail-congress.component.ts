import { Component } from '@angular/core';
import { CongressService } from '../../Services/Congress/congress.service';
import { CongressResponse } from '../../interfaces/Congress';
import { Activity } from '../../interfaces/Activity';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ActivityCardComponent } from '../../Activities/activity-card/activity-card.component';
import { AuthService } from '../../Services/Auth/auth.service';
import { InscriptionService } from '../../Services/Inscription/inscription.service';
import { AttendanceService } from '../../Services/Attendance/attendance.service';
import { CreateAtteendance } from '../../interfaces/Atteendance';
import { AttendanceType } from '../../interfaces/Enums';
import { UserService } from '../../Services/User/user.service';
import Swal from 'sweetalert2';

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
  userId: number = 0
  constructor(private servicio: CongressService, private route: ActivatedRoute, private authService: AuthService, private attendanceService: AttendanceService,
    private userService: UserService
  ) { }
  ngOnInit() {
    this.isLoggedIn = this.authService.isLoggedIn()
    this.userId = this.authService.getUserId()
    this.congressId = parseInt(this.route.snapshot.paramMap.get('id')!)
    this.servicio.getCongressById(this.congressId).subscribe({
      next: (response) => { this.congress = response }
    })
    this.servicio.getActivitiesByCongressId(this.congressId).subscribe({
      next: (response) => { this.activities = response }
    })
  }
  enrollWorkshop(activityId: number) {
    // Primero pedir la fecha con Swal
    Swal.fire({
      title: 'Fecha de inscripción',
      html: `
      <input type="datetime-local" id="attendanceDate"
        class="swal2-input"
        style="width: 90%"
        value="${new Date().toISOString().slice(0, 16)}" />
    `,
      confirmButtonText: 'Confirmar inscripción',
      cancelButtonText: 'Cancelar',
      showCancelButton: true,
      background: '#0f172a',
      color: '#f1f5f9',
      confirmButtonColor: '#0ea5e9',
      preConfirm: () => {
        const input = document.getElementById('attendanceDate') as HTMLInputElement;
        if (!input.value) {
          Swal.showValidationMessage('Debes seleccionar una fecha');
          return false;
        }
        return input.value;
      }
    }).then(result => {
      if (!result.isConfirmed) return;

      const date = result.value;

      this.userService.getUserById(this.userId).subscribe({
        next: (response) => {
          const data: CreateAtteendance = {
            activityId,
            userIdentification: response.identification,
            type: AttendanceType.WORKSHOPINSCRIPTION,
            date: date
          };
          this.attendanceService.create(data).subscribe({
            next: () => Swal.fire({ title: 'OK', text: 'Registro completado', icon: 'success', background: '#0f172a', color: '#f1f5f9' }),
            error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
          });
        }
      });
    });
  }
}
