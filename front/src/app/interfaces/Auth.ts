export interface LoginResponse {
  accesToken: string;
  tokenType: string; 
  expiresIn: number;
}

export interface Login{
    email:string,
    password:string
}
