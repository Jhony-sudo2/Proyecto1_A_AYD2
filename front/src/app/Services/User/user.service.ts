import { Injectable } from '@angular/core';
import { environemnt } from '../../../environment/Environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Organization } from '../../interfaces/Organization';
import { CreateUser, Rol, UpdatePassword, UpdateUser, User } from '../../interfaces/User';
import { Wallet, WalletRecharge } from '../../interfaces/Wallet';

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

  getUserById(id:number):Observable<User>{
    return this.http.get<User>(`${this.baseUrl}/${id}`)
  }

  changeState(userId:number){
    return this.http.patch<User>(`${this.baseUrl}/${userId}`,{})
  }

  getAllUser():Observable<User[]>{
    return this.http.get<User[]>(this.baseUrl)
  }

  getWalletByUserId(userId:number):Observable<Wallet>{
    return this.http.get<Wallet>(`${this.baseUrl}/${userId}/wallet`)
  }

  getRechargeHistory(userId:number):Observable<WalletRecharge>{
    return this.http.get<WalletRecharge>(`${this.baseUrl}/${userId}/wallet/recharges`)
  }

  rechargeWallet(data:WalletRecharge,userId:number):Observable<Wallet>{
    return this.http.put<Wallet>(`${this.baseUrl}/${userId}/wallet`,data)
  }

  getOrganizations():Observable<Organization[]>{
    return this.http.get<Organization[]>(this.organizationUrl)
  }
  udpatePassword(data:UpdatePassword,id:number){
    return this.http.put(`${this.baseUrl}/${id}/password`,data)
  }

  updateUser(data:UpdateUser,id:number){
    return this.http.put(`${this.baseUrl}/${id}`,data)
  }

  getOrganizationByUserId(id:number):Observable<Organization>{
    return this.http.get<Organization>(`${this.organizationUrl}/${id}`)
  }

  recoveryPassword(email:string){
    const data = {email}
    return this.http.post(`${this.baseUrl}/password/recovery`,data)
  }

  confirCode(email:string,code:string,newPassword:string){
    const data = {email,code,newPassword}
    return this.http.post(`${this.baseUrl}/password/confirm`,data)
  }

  getAllRols():Observable<Rol[]>{
    return this.http.get<Rol[]>(`${this.baseUrl}/rols`)
  }
}
