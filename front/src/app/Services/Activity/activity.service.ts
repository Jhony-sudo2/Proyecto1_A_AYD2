import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environemnt } from '../../../environment/Environment';
import { Activity, CreateActivity, CreateProposal, Proposal } from '../../interfaces/Activity';
import { Observable } from 'rxjs';
import { ProposalState } from '../../interfaces/Enums';

@Injectable({
  providedIn: 'root'
})
export class ActivityService {
  baseUrl = environemnt.baseUrl + '/activities'
  constructor(private http: HttpClient) { }

  createActivity(data: CreateActivity): Observable<Activity> {
    return this.http.post<Activity>(this.baseUrl, data);
  }

  getActivitiesByCongressId(congressId: number): Observable<Activity[]> {
    return this.http.get<Activity[]>(`${this.baseUrl}/${congressId}`);
  }

  createProposal(data: CreateProposal): Observable<Proposal> {
    return this.http.post<Proposal>(`${this.baseUrl}/proposal`, data);
  }

  getProposalById(id: number): Observable<Proposal> {
    return this.http.get<Proposal>(`${this.baseUrl}/proposal/${id}`);
  }

  getProposalByUserId(userId: number): Observable<Proposal> {
    return this.http.get<Proposal>(`${this.baseUrl}/proposal/user/${userId}`);
  }


  getProposalsByCongressId(congressId: number): Observable<Proposal[]> {
    return this.http.get<Proposal[]>(`${this.baseUrl}/congress/${congressId}`);
  }

  getProposalByCongressIdAndState(congressId: number,state:ProposalState): Observable<Proposal[]> {
    return this.http.get<Proposal[]>(`${this.baseUrl}/congress/${congressId}/${state}`);
  }

  updateProposal(id: number, data: ProposalState): Observable<Proposal> {
    const dataa = {state:data}
    return this.http.put<Proposal>(`${this.baseUrl}/proposal/${id}`, dataa);
  }


}
