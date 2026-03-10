import { Component } from '@angular/core';
import { UserService } from '../../Services/User/user.service';
import { AuthService } from '../../Services/Auth/auth.service';
import { Inscription } from '../../interfaces/Inscription';
import { Certificate } from '../../interfaces/User';
import { InscriptionService } from '../../Services/Inscription/inscription.service';
import { Pdf } from '../../Utils/PdfCreator';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AttendanceService } from '../../Services/Attendance/attendance.service';
import { Activity } from '../../interfaces/Activity';

@Component({
  selector: 'app-my-certificates',
  imports: [CommonModule, RouterModule],
  templateUrl: './my-certificates.component.html',
  styleUrl: './my-certificates.component.css'
})
export class MyCertificatesComponent {
  inscriptions: Inscription[] = [];
  certificates: Certificate[] = [];
  workshopInscriptions: Activity[] = [];
  userId: number = 0;
  pdfUtil: Pdf = new Pdf();
  selectedKey: string | null = null;
  loadingCerts: boolean = false;
  activePanel: 'certificates' | 'workshops' = 'certificates';

  constructor(
    private service: UserService,
    private authService: AuthService,
    private inscriptionService: InscriptionService,
    private router: Router,
    private attendanceService: AttendanceService
  ) { }

  ngOnInit() {
    this.userId = this.authService.getUserId();
    this.inscriptionService.getInscriptionsByUserId(this.userId).subscribe({
      next: (response) => { this.inscriptions = response; }
    });
  }

  getKey(ins: Inscription): string {
    return `${ins.congressId}-${ins.attendeeRolName}`;
  }

  togglePanel(ins: Inscription, panel: 'certificates' | 'workshops') {
    const key = this.getKey(ins);

    if (this.selectedKey === key && this.activePanel === panel) {
      this.selectedKey = null;
      this.certificates = [];
      this.workshopInscriptions = [];
      return;
    }

    this.selectedKey = key;
    this.activePanel = panel;

    if (panel === 'certificates') {
      this.loadingCerts = true;
      this.certificates = [];
      this.service.getCertificates(this.userId, ins.congressId).subscribe({
        next: (response) => { this.certificates = response; this.loadingCerts = false; },
        error: () => { this.loadingCerts = false; }
      });
    }

    if (panel === 'workshops') {
      this.workshopInscriptions = [];
      this.attendanceService.getWorkshopInscription(this.userId, ins.congressId).subscribe({
        next: (response) => { this.workshopInscriptions = response; }
      });
    }
  }

  goToDetail(congressId: number) {
    this.router.navigate(['/congressDetails', congressId]);
  }

  pdf(certificate: Certificate) {
    this.pdfUtil.generarPdfCertificate(certificate);
  }
}