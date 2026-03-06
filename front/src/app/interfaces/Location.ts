export interface Location{
    id: number,
    name: string,
    address: string,
    city: string,
    country: string
}

export interface CreateLocation{
    name: string,
    address: string,
    city: string,
    country: string
}

export interface Room{
    id:number,
    name:string,
    capacity:number,
    equipment:string,
    description:string
}

export interface CreateRoom{
    name:string,
    capacity:number,
    equipment:string,
    description:string
}