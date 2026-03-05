export interface CreateCongress{
    name:string
    description:string,
    price:number,
    organizationId:number,
    locationId:number,
    endCallDate:Date
    startDate:Date,
    endDate:Date,
    imageUrl:string
}

export interface CongressResponse{
   id: number,
   name: string,
   description: string,
   price: number,
   imageUrl: string,
   startDate: Date,
   endDate: Date,
   endCallDate: Date,
   organizationName: string,
   locationName: string
}