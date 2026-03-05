export interface Activity{
    id:number,
    name:string,
    description:string,
    startDate:Date,
    endDate:Date,
    type:string,
    capacity:number
}

export interface CreateActivity{
  roomId: number,
  proposalId: number,
  startDate: Date,
  endDate: Date,
  capacity: number
}