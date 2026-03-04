export interface LoginResponse {
  token: string;
  tokenType: string; 
  expSeconds: number;
}

export interface Login{
    email:string,
    password:string
}
