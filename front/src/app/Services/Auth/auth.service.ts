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

  login(data: Login) {

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

  private decodeJwtPayload(token: string): any | null {
    try {
      const parts = token.split('.');
      if (parts.length !== 3) return null;
      const payload = parts[1];
      const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      const padded = base64.padEnd(base64.length + (4 - (base64.length % 4)) % 4, '=');

      const json = atob(padded);
      return JSON.parse(json);
    } catch {
      return null;
    }
  }

  getUserId(): number {
    const token = this.getToken();
    if (!token) return 0;

    const payload = this.decodeJwtPayload(token);
    if (!payload) return 0;

    const raw =
      payload.id 
    const id = Number(raw);
    return Number.isFinite(id) ? id : 0;
  }

}
