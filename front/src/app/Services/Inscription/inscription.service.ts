import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environemnt } from '../../../environment/Environment';
import { Inscription, Pay, PayCongress } from '../../interfaces/Inscription';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class InscriptionService {
  baseUrl = environemnt.baseUrl + '/inscriptions'
  constructor(private http:HttpClient) { }

  pay(data:PayCongress){
    return this.http.post(this.baseUrl+'/pay',data)
  }

  getInscriptionsByUserId(userId:number):Observable<Inscription[]>{
    return this.http.get<Inscription[]>(`${this.baseUrl}/${userId}`)
  }

  getPaymentsByUserId(userId:number):Observable<Pay[]>{
    return this.http.get<Pay[]>(`${this.baseUrl}/pay/${userId}`)
  }

  

}
