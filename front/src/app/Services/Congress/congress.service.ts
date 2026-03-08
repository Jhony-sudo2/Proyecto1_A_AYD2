import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environemnt } from '../../../environment/Environment';
import { CongressResponse, CreateCongress } from '../../interfaces/Congress';
import { Observable } from 'rxjs';
import { Location } from '../../interfaces/Location';
import { Activity } from '../../interfaces/Activity';
import { User } from '../../interfaces/User';

@Injectable({
  providedIn: 'root'
})
export class CongressService {
  baseUrl = environemnt.baseUrl + '/congresses'
  activityUrl = environemnt.baseUrl + '/activities'
  constructor(private http:HttpClient) { }

  createCongress(data:CreateCongress):Observable<CongressResponse>{
    return this.http.post<CongressResponse>(this.baseUrl,data);
  }

  getLocations():Observable<Location[]>{
    return this.http.get<Location[]>(environemnt.baseUrl + '/locations');
  }

  getCongressById(id:number):Observable<CongressResponse>{
    return this.http.get<CongressResponse>(`${this.baseUrl}/${id}`)
  }

  getActivitiesByCongressId(id:number):Observable<Activity[]>{
    return this.http.get<Activity[]>(`${this.activityUrl}/${id}`)
  }

  getAllCongress():Observable<CongressResponse[]>{
    return this.http.get<CongressResponse[]>(this.baseUrl)
  }

  saveCommite(congressId:number,userId:number){
    const data = {userId}
    return this.http.post(`${this.baseUrl}/${congressId}/committee`,data)
  }

  getCommite(congressId:number):Observable<User[]>{
    return this.http.get<User[]>(`${this.baseUrl}/${congressId}/committee`)
  }

  deleteCommite(congressId:number,userId:number){
    return this.http.delete(`${this.baseUrl}/${congressId}/committee/${userId}`)
  }

}
