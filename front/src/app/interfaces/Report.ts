import { CongressResponse } from "./Congress";

// Filtros de envío
export interface InscriptionFilter {
  congressId: number;
  attendeeType?: number;
}

export interface AttendanceReportRequest {
  activityId?: number;
  roomId?: number;
  startDate?: string;
  endDate?: string;
}

export interface WorkshopReportFilter {
  congressId: number;
  activityId?: number;
}

export interface EarningCongressFilter {
  congressId?: number;
  startDate?: string;
  endDate?: string;
}

export interface EarningFilter {
  startDate: string;
  endDate: string;
  organizationId?: number;
}

// Respuestas recibidas
export interface InscriptionReport {
  identification: string;
  name: string;
  organizationName: string;
  email: string;
  phone: string;
  attendeeRolName: string;
}

export interface AttendanceReport {
  activityName: string;
  roomName: string;
  startDate: string;
  attendances: number;
}

export interface WorkshopParticipant {
  identification: string;
  fullName: string;
  email: string;
  attendeeRolName: string;
}

export interface WorkshopReport {
  workshopName: string;
  capacity: number;
  total: number;
  available: number;
  participants: WorkshopParticipant[];
}

export interface EarningCongressReport {
  congress: CongressResponse;
  total: number;
  commission: number;
  earning: number;
}

export interface EarningReport {
  congressName: string;
  endDate: string;
  startDate: string;
  locationName: string;
  organizationName: string;
  totalCollected: number;
  totalProfit: number;
}
