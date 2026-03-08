import { AttendanceType } from "./Enums";

export interface CreateAtteendance{
    activityId: number,
    userIdentification:string,
    date: string,
    type: AttendanceType
}

export interface Atteendance{
    userId:number,
    userName:string,
    type:AttendanceType
}