import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environemnt } from '../../../environment/Environment';
import { Observable } from 'rxjs';
import { SysConfig } from '../../interfaces/SystemConfiguration';

@Injectable({
  providedIn: 'root'
})
export class SystemService {
  baseUrl = environemnt.baseUrl +'/systemconfigurations'
  constructor(private http:HttpClient) { }

  getConfiguration():Observable<SysConfig>{
    return this.http.get<SysConfig>(this.baseUrl)
  }

  updateConfiguration(data:SysConfig){
    return this.http.put<SysConfig>(this.baseUrl,data)
  }

}
