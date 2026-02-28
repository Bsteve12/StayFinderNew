import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AlojamientosService {

  //ruta = "https://stayfinder1-production.up.railway.app/api/alojamientos";
  ruta = `${environment.apiUrl}/api/alojamientos`;
  constructor(private http: HttpClient) {}

  obtenerAlojamientosActivos(): Observable<any> {
    return this.http.get(`${this.ruta}/activos`);
  }

  obtenerAlojamientoPorId(id: number): Observable<any> {
    return this.http.get(`${this.ruta}/${id}`);
  }

}
