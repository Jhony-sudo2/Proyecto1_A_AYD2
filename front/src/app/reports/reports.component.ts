import { Component } from '@angular/core';
import { CongressService } from '../Services/Congress/congress.service';
import { ActivityService } from '../Services/Activity/activity.service';
import { Activity } from '../interfaces/Activity';
import { ReportService } from '../Services/Reports/report.service';
import { CongressResponse } from '../interfaces/Congress';
import Swal from 'sweetalert2';
import { AttendanceReport, AttendanceReportRequest, EarningCongressFilter, EarningCongressReport, EarningFilter, EarningReport, InscriptionFilter, InscriptionReport, WorkshopReport, WorkshopReportFilter } from '../interfaces/Report';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-reports',
  imports: [CommonModule,FormsModule],
  templateUrl: './reports.component.html',
  styleUrl: './reports.component.css'
})
export class ReportsComponent {
  congress: CongressResponse[] = []
  activities: Activity[] = []
  constructor(private service: ReportService, private activityService: ActivityService, private congressService: CongressService) { }

  ngOnInit() {
    this.congressService.getAllCongress().subscribe({
      next: (response) => { this.congress = response }
    })
  }
  // Propiedades
  activeTab: string = 'participants';

  mainTabs = [
    { id: 'participants', label: '👥 Participantes' },
    { id: 'attendance', label: '✅ Asistencia' },
    { id: 'workshops', label: '🛠️ Talleres' },
    { id: 'earningsCongress', label: '💰 Ganancias congreso' },
    { id: 'earnings', label: '📊 Ganancias sistema' },
  ];

  // Filtros
  inscriptionFilter: InscriptionFilter = { congressId: 0 };
  attendanceFilter: AttendanceReportRequest = {};
  workshopFilter: WorkshopReportFilter = { congressId: 0 };
  earningCongressFilter: EarningCongressFilter = {};
  earningFilter: EarningFilter = { startDate: '', endDate: '' };

  // Resultados
  inscriptionReports: InscriptionReport[] = [];
  attendanceReports: AttendanceReport[] = [];
  workshopReports: WorkshopReport[] = [];
  earningCongressReports: EarningCongressReport[] = [];
  earningReports: EarningReport[] = [];
  earningsLoaded: boolean = false;
  selectedCongressForActivity: number = 0;

  // Totales ganancias sistema
  get totalCollected(): number { return this.earningReports.reduce((s, r) => s + r.totalCollected, 0); }
  get totalProfit(): number { return this.earningReports.reduce((s, r) => s + r.totalProfit, 0); }

  // Agrupado por institución (ordenado alfabéticamente)
  get earningGrouped(): { organization: string, reports: EarningReport[] }[] {
    const map = new Map<string, EarningReport[]>();
    this.earningReports.forEach(r => {
      if (!map.has(r.organizationName)) map.set(r.organizationName, []);
      map.get(r.organizationName)!.push(r);
    });
    return Array.from(map.entries())
      .sort(([a], [b]) => a.localeCompare(b))
      .map(([organization, reports]) => ({
        organization,
        reports: reports.sort((a, b) => b.totalProfit - a.totalProfit) // mayor a menor ganancia
      }));
  }

  loadActivitiesForReport(congressId: number) {
    if (!congressId) return;
    this.activityService.getActivitiesByCongressId(congressId).subscribe({
      next: (res) => { this.activities = res; }
    });
  }

  loadParticipants() {
    this.service.getInscriptionReport(this.inscriptionFilter).subscribe({
      next: (res) => { this.inscriptionReports = res; },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }

  loadAttendance() {
    this.service.getAttendanceReport(this.attendanceFilter).subscribe({
      next: (res) => { this.attendanceReports = res; },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }

  loadWorkshops() {
    this.service.getWorkshopReport(this.workshopFilter).subscribe({
      next: (res) => { this.workshopReports = res; },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }

  loadEarningsCongress() {
    this.service.getEarningsCongressReport(this.earningCongressFilter).subscribe({
      next: (res) => { this.earningCongressReports = res; },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }

  loadEarnings() {
    this.service.getEarningsReport(this.earningFilter).subscribe({
      next: (res) => { this.earningReports = res; this.earningsLoaded = true; },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }

}
