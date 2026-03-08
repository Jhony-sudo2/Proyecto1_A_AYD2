import { ProposalState, ProposalType } from "./Enums"


export interface Activity {
  id: number,
  name: string,
  description: string,
  roomId:number
  startDate: string,
  endDate: string,
  type: string,
  capacity: number
}

export interface CreateActivity {
  name:string
  roomId: number,
  proposalId: number,
  startDate: Date,
  endDate: Date,
  capacity: number
}

export interface updateActivity{
  startDate: Date,
  endDate: Date,
  capacity: number
  roomId:number,
  name:string
}

export interface Proposal {
  id:number
  name:string,
  congressName:string,
  userName:string,
  description:string,
  type:ProposalType,
  state:ProposalState
}

export interface CreateProposal {
  congressId: number,
  userId: number,
  name: string,
  description: string,
  type: ProposalType
}