import { Injectable } from '@angular/core';
import { environemnt } from '../../../environment/Environment';
import { HttpClient } from '@angular/common/http';
import { Login, LoginResponse } from '../../interfaces/Auth';
import { tap } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly baseUrl = environemnt.baseUrl + '/auth';
  private readonly TOKEN_COOKIE = 'access_token';

  constructor(private http: HttpClient, private cookies: CookieService) { }

  login(data:Login) {
  
    return this.http.post<LoginResponse>(this.baseUrl, data).pipe(
      tap((res) => {
        console.log(res);
        this.cookies.set(
          this.TOKEN_COOKIE,
          res.accesToken,
          undefined, 
          '/',       
          undefined, 
          false      
        );
      })
    );
  }

  getToken(): string | null {
    const t = this.cookies.get(this.TOKEN_COOKIE);
    return t ? t : null;
  }

  logout(): void {
    this.cookies.delete(this.TOKEN_COOKIE, '/');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
