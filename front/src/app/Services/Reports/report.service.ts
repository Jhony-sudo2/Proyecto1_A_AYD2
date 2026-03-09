import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environemnt } from '../../../environment/Environment';
import { AttendanceReport, AttendanceReportRequest, EarningCongressFilter, EarningCongressReport, EarningFilter, EarningReport, InscriptionFilter, InscriptionReport, WorkshopReport, WorkshopReportFilter } from '../../interfaces/Report';
import { Observable } from 'rxjs';
import { CongressResponse } from '../../interfaces/Congress';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  baseUrl = environemnt.baseUrl + '/reports';

  constructor(private http: HttpClient) { }

  getEarningsReport(data: EarningFilter): Observable<EarningReport[]> {
    let params = new HttpParams()
      .set('startDate', data.startDate.substring(0, 10))  
      .set('endDate', data.endDate.substring(0, 10));
    if (data.organizationId !== null && data.organizationId !== undefined)
      params = params.set('organizationId', data.organizationId);
    return this.http.get<EarningReport[]>(`${this.baseUrl}/earnings`, { params });
  }

  getCongressByOrganization(organizationId: number): Observable<CongressResponse[]> {
    return this.http.get<CongressResponse[]>(`${this.baseUrl}/organizations/${organizationId}/congress`);
  }

  getInscriptionReport(filter: InscriptionFilter): Observable<InscriptionReport[]> {
    return this.http.post<InscriptionReport[]>(`${this.baseUrl}/participants`, filter);
  }

  getAttendanceReport(request: AttendanceReportRequest): Observable<AttendanceReport[]> {
    return this.http.post<AttendanceReport[]>(`${this.baseUrl}/attendance`, request);
  }

  getWorkshopReport(filter: WorkshopReportFilter): Observable<WorkshopReport[]> {
    return this.http.post<WorkshopReport[]>(`${this.baseUrl}/workshops`, filter);
  }

  getEarningsCongressReport(filter: EarningCongressFilter): Observable<EarningCongressReport[]> {
    return this.http.post<EarningCongressReport[]>(`${this.baseUrl}/congress/earnings`, filter);
  }
}
