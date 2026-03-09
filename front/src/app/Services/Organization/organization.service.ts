import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environemnt } from '../../../environment/Environment';
import { NewOrganizationRequest, Organization, OrganizationUpdate } from '../../interfaces/Organization';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OrganizationService {
  baseUrl = environemnt.baseUrl + '/organizations'
  constructor(private http:HttpClient) { }
  create(request: NewOrganizationRequest): Observable<Organization> {
    return this.http.post<Organization>(this.baseUrl, request);
  }

  getAll(): Observable<Organization[]> {
    return this.http.get<Organization[]>(this.baseUrl);
  }

  getById(id: number): Observable<Organization> {
    return this.http.get<Organization>(`${this.baseUrl}/${id}`);
  }

  update(id: number, request: OrganizationUpdate): Observable<Organization> {
    return this.http.put<Organization>(`${this.baseUrl}/${id}`, request);
  }
}
