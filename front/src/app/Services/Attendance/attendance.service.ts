import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environemnt } from '../../../environment/Environment';
import { Atteendance, CreateAtteendance } from '../../interfaces/Atteendance';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AttendanceService {
  baseUrl = environemnt.baseUrl + '/atteendances'
  constructor(private http:HttpClient) { }

  create(data:CreateAtteendance){
    return this.http.post(this.baseUrl,data)
  }

  getAtteendancesByUserId(userId:number):Observable<Atteendance[]>{
    return this.http.get<Atteendance[]>(`${this.baseUrl}/users/${userId}`)
  }

  getAttendancesByActivity(activityId:number):Observable<Atteendance[]>{
    return this.http.get<Atteendance[]>(`${this.baseUrl}/activities/${activityId}`)
  }


}
