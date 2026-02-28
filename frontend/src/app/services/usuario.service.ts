import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface CreateUserDTO {
  nombre: string;
  correo: string;
  telefono: string;
  fechaNacimiento: string;
  usuarioId: number;
  contrasena: string;
}

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  private API_URL = `${environment.apiUrl}/api/usuario`;

  constructor(private http: HttpClient) {}

  // Registro de usuario
  register(data: CreateUserDTO): Observable<any> {
    return this.http.post(this.API_URL, data);
  }
}
