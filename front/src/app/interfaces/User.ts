export interface Rol{
    id:number,
    name:string
}

export interface CreateUser {
    identification: string,
    name: string,
    lastName: string,
    email: string,
    phone: string,
    imageUrl: string,
    nacionality: string,
    organization: number,
    password: string
}
export interface CreateUserWithRol extends CreateUser {
    rol: number
}

export interface User {
    id: number;
    identification: string;
    name: string;
    lastName: string;
    email: string;
    phone: string;
    imageUrl: string;
    active: boolean;
    nacionality: string;
    organizationName  : string;
}

export interface UpdateUser{
    name: string;
    lastName: string;
    email: string;
    phone: string;
    image: string;
}

export interface UpdatePassword{
    currentPassword: string,
    newPassword: string
}

export interface Certificate {
  congressName: string;
  startDate: Date;
  endDate: Date;
  locationName: string;
  name: string;
  lastName: string;
  date: Date;
  organizationName: string;
  assitantType:String
}