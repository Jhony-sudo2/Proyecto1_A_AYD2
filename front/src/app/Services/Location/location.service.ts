import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environemnt } from '../../../environment/Environment';
import { CreateLocation, CreateRoom, Location, Room } from '../../interfaces/Location';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LocationService {
  baseUrl = environemnt.baseUrl + '/locations'
  constructor(private htpp:HttpClient) { }

  createLocation(data:CreateLocation):Observable<Location>{
    return this.htpp.post<Location>(this.baseUrl,data)
  }

  getAllLocations():Observable<Location[]>{
    return this.htpp.get<Location[]>(this.baseUrl)
  }

  getLocationById(id:number):Observable<Location>{
    return this.htpp.get<Location>(`${this.baseUrl}/${id}`)
  }

  createRomm(data:CreateRoom,locationId:number):Observable<Room>{
    return this.htpp.post<Room>(`${this.baseUrl}/${locationId}/rooms`,data)
  }

  getRoomsByLocationId(locationId:number):Observable<Room[]>{
    return this.htpp.get<Room[]>(`${this.baseUrl}/${locationId}/rooms`)
  }

  updateRoom(data:CreateRoom,id:number):Observable<Room>{
    return this.htpp.put<Room>(`${this.baseUrl}/rooms/${id}`,data)
  }

  deleteRoom(id:number){
    return this.htpp.delete(`${this.baseUrl}/rooms/${id}`)
  }

}
