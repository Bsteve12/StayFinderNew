import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AlojamientoRequestDTO, AlojamientoResponseDTO } from '../models/alojamiento.model';

@Injectable({
  providedIn: 'root',
})
export class AlojamientosService {

  //ruta = "https://stayfinder1-production.up.railway.app/api/alojamientos";
  ruta = `${environment.apiUrl}/api/alojamientos`;
  constructor(private http: HttpClient) { }

  crearAlojamiento(data: AlojamientoRequestDTO, ownerId: number): Observable<AlojamientoResponseDTO> {
    const params = new HttpParams().set('ownerId', ownerId.toString());
    return this.http.post<AlojamientoResponseDTO>(this.ruta, data, { params });
  }

  editarAlojamiento(id: number, data: AlojamientoRequestDTO, ownerId: number): Observable<AlojamientoResponseDTO> {
    const params = new HttpParams().set('ownerId', ownerId.toString());
    return this.http.put<AlojamientoResponseDTO>(`${this.ruta}/${id}`, data, { params });
  }

  eliminarAlojamiento(id: number, ownerId: number): Observable<void> {
    const params = new HttpParams().set('ownerId', ownerId.toString());
    return this.http.delete<void>(`${this.ruta}/${id}`, { params });
  }

  obtenerAlojamientosDeOwner(ownerId: number): Observable<AlojamientoResponseDTO[]> {
    return this.http.get<AlojamientoResponseDTO[]>(`${this.ruta}/owner/${ownerId}`);
  }

  obtenerAlojamientosActivos(): Observable<AlojamientoResponseDTO[]> {
    return this.http.get<AlojamientoResponseDTO[]>(`${this.ruta}/activos`);
  }

  obtenerAlojamientoPorId(id: number): Observable<AlojamientoResponseDTO> {
    return this.http.get<AlojamientoResponseDTO>(`${this.ruta}/${id}`);
  }

}
