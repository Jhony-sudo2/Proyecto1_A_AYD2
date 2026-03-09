export interface Organization{
    id:number,
    name:string,
    image:string,
    canCreateCongress:boolean
}

export interface NewOrganizationRequest {
  name: string;
  image: string;
}
export interface OrganizationUpdate {
  name: string;
  image: string;
  canCreateCongress: boolean;
}
