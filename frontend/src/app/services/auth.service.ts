import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

export interface LoginRequest {
  email: string;
  contrasena: string;
}

export interface LoginResponse {
  token: string;
}

export interface User {
  id?: number | null;
  nombre?: string | null;
  email?: string | null;
  role?: 'CLIENT' | 'OWNER' | 'ADMIN' | undefined;
  usuarioId?: number | null;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  //  URL CORRECTA DEL BACKEND
  //private apiUrl = 'https://stayfinder-backend-86433570710.us-central1.run.app/api/usuario'; // Descomentar cuando ya estemos en produccion
  private apiUrl = `${environment.apiUrl}/api/usuario`;

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  private currentUserSubject = new BehaviorSubject<User | null>(null);

  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    this.checkInitialAuth();
  }

  private checkInitialAuth() {
    const token = localStorage.getItem('token');
    if (token) {
      const user = this.buildUserFromToken(token);
      if (user) {
        this.isAuthenticatedSubject.next(true);
        this.currentUserSubject.next(user);
        return;
      }
    }
    // Si no hay token al iniciar la app, limpiamos posibles basuras,
    // pero NO forzamos una redirección a /login para permitir entrar a rutas públicas.
    this.clearAuthData();
  }

  //  LOGIN CORRECTO
  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((response) => {
        if (response.token) {
          localStorage.setItem('token', response.token);
          const user = this.buildUserFromToken(response.token);
          if (user) {
            localStorage.setItem('user', JSON.stringify(user));
            if (user.role) localStorage.setItem('role', user.role);
            this.isAuthenticatedSubject.next(true);
            this.currentUserSubject.next(user);
          }
        }
      })
    );
  }

  //  REGISTRO CORRECTO
  register(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}`, userData);
  }

  private clearAuthData() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('role');
    this.isAuthenticatedSubject.next(false);
    this.currentUserSubject.next(null);
  }

  logout() {
    this.clearAuthData();
    this.router.navigate(['/login']);
  }

  // En auth.service.ts
  forgotPassword(email: string): Observable<any> {
    // Ajustamos el envío para que coincida con el RequestBody de tu Java
    // Si tu DTO pide "email", envíalo así:
    return this.http.post(`${environment.apiUrl}/api/auth/forgot-password`, { email: email });
  }



  resetPassword(token: string, nuevaPassword: string): Observable<any> {
    const url = `${environment.apiUrl}/api/auth/reset-password`;

    // Usamos HttpParams nativo de Angular para evitar problemas con símbolos + & = 
    // en la contraseña que deformen la URL
    let params = new HttpParams()
      .set('token', token)
      .set('nuevaPassword', nuevaPassword);

    return this.http.post(url, {}, { params: params });
  }



  private buildUserFromToken(token: string): User | null {
    try {
      const parts = token.split('.');
      if (parts.length < 2) return null;

      const payload = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')));
      console.log('Token Payload decodificado:', payload);

      // El backend guarda el rol en el 'iss' (issuer) por ejemplo "ROLE_ADMIN", "ADMIN" o "CLIENT"
      const rawRole = payload.iss || payload.rol;
      let finalRole = rawRole ? rawRole.toUpperCase() : undefined;

      // Limpiamos el prefijo 'ROLE_' si existe para que coincida con 'ADMIN', 'CLIENT', 'OWNER'
      if (finalRole && finalRole.startsWith('ROLE_')) {
        finalRole = finalRole.replace('ROLE_', '');
      }

      return {
        usuarioId: payload.usuarioId || null,
        id: payload.usuarioId || null,
        email: payload.sub || null,
        nombre: payload.nombre || null,
        role: finalRole as 'CLIENT' | 'OWNER' | 'ADMIN' | undefined
      };
    } catch (e) {
      return null;
    }
  }
}
