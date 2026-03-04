import { Injectable } from '@angular/core';
import { environemnt } from '../../../environment/Environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Organization } from '../../interfaces/Organization';
import { CreateUser } from '../../interfaces/User';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl = environemnt.baseUrl + '/users'
  private organizationUrl = environemnt.baseUrl + '/organizations'
  constructor(private http:HttpClient) { }

  saveUser(data:CreateUser){
    return this.http.post(this.baseUrl,data)
  }

  getOrganizations():Observable<Organization[]>{
    return this.http.get<Organization[]>(this.baseUrl)
  }


}
