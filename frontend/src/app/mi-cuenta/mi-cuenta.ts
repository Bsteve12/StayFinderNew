import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { Observable } from 'rxjs';

// Interfaces
interface ReservaResponseDTO {
  id: number;
  alojamientoNombre: string;
  fechaInicio: string;
  fechaFin: string;
  estado: string;
  precioTotal: number;
  alojamientoImagen?: string;
}

interface FavoriteResponseDTO {
  id: number;
  alojamientoId: number;
  alojamientoNombre: string;
  alojamientoImagen: string;
  alojamientoUbicacion: string;
  alojamientoPrecio: number;
  fechaAgregado: string;
}

interface HistorialReservasRequestDTO {
  fechaInicio?: string;
  fechaFin?: string;
  estado?: string;
}

interface ReservaHistorialResponseDTO {
  id: number;
  alojamientoNombre: string;
  alojamientoImagen: string;
  fechaInicio: string;
  fechaFin: string;
  estado: string;
  precioTotal: number;
  fechaReserva: string;
}

@Component({
  selector: 'app-mi-cuenta',
  imports: [CommonModule, HttpClientModule, ButtonModule, CardModule],
  templateUrl: './mi-cuenta.html',
  styleUrl: './mi-cuenta.scss',
})
export class MiCuenta implements OnInit {
  private readonly API_URL = 'http://localhost:8080/api';
  
  // Estado de la vista actual
  currentView: 'profile' | 'reservas' | 'favoritos' | 'historial' = 'profile';
  
  // Usuario actual (simulado - debería venir del servicio de autenticación)
  usuarioId: number = 1;
  usuarioNombre: string = 'Juan Pérez';
  usuarioEmail: string = 'juan.perez@example.com';
  
  // Datos
  reservas: ReservaResponseDTO[] = [];
  favoritos: FavoriteResponseDTO[] = [];
  historial: ReservaHistorialResponseDTO[] = [];
  
  // Loading states
  loadingReservas: boolean = false;
  loadingFavoritos: boolean = false;
  loadingHistorial: boolean = false;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadReservas();
  }

  // ============================================
  // 🔹 Cambiar Vista
  // ============================================
  changeView(view: 'profile' | 'reservas' | 'favoritos' | 'historial') {
    this.currentView = view;
    
    // Cargar datos según la vista
    switch(view) {
      case 'reservas':
        this.loadReservas();
        break;
      case 'favoritos':
        this.loadFavoritos();
        break;
      case 'historial':
        this.loadHistorial();
        break;
    }
  }

  // ============================================
  // 🔹 Cargar Reservas Activas
  // ============================================
  loadReservas() {
    if (this.loadingReservas) return;
    
    this.loadingReservas = true;
    // Simulación - reemplazar con llamada real al API
    setTimeout(() => {
      this.reservas = [
        {
          id: 1,
          alojamientoNombre: 'Casa en la Playa',
          fechaInicio: '2025-12-01',
          fechaFin: '2025-12-05',
          estado: 'CONFIRMADA',
          precioTotal: 750000,
          alojamientoImagen: 'https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?w=400'
        },
        {
          id: 2,
          alojamientoNombre: 'Apartamento Moderno',
          fechaInicio: '2025-11-20',
          fechaFin: '2025-11-25',
          estado: 'PENDIENTE',
          precioTotal: 600000,
          alojamientoImagen: 'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=400'
        }
      ];
      this.loadingReservas = false;
    }, 500);
  }

  // ============================================
  // 🔹 Cargar Favoritos
  // ============================================
  loadFavoritos() {
    if (this.loadingFavoritos) return;
    
    this.loadingFavoritos = true;
    this.http.get<FavoriteResponseDTO[]>(`${this.API_URL}/favoritos/usuario/${this.usuarioId}`)
      .subscribe({
        next: (data) => {
          this.favoritos = data;
          this.loadingFavoritos = false;
        },
        error: (error) => {
          console.error('Error cargando favoritos:', error);
          this.loadingFavoritos = false;
          // Datos de ejemplo en caso de error
          this.favoritos = [
            {
              id: 1,
              alojamientoId: 1,
              alojamientoNombre: 'Villa Campestre',
              alojamientoImagen: 'https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=400',
              alojamientoUbicacion: 'Medellín, Colombia',
              alojamientoPrecio: 200000,
              fechaAgregado: '2025-11-01'
            }
          ];
        }
      });
  }

  // ============================================
  // 🔹 Cargar Historial
  // ============================================
  loadHistorial(filtros?: HistorialReservasRequestDTO) {
    if (this.loadingHistorial) return;
    
    this.loadingHistorial = true;
    
    let url = `${this.API_URL}/reservas/usuario/${this.usuarioId}`;
    const params: string[] = [];
    
    if (filtros?.fechaInicio) params.push(`fechaInicio=${filtros.fechaInicio}`);
    if (filtros?.fechaFin) params.push(`fechaFin=${filtros.fechaFin}`);
    if (filtros?.estado) params.push(`estado=${filtros.estado}`);
    
    if (params.length > 0) {
      url += '?' + params.join('&');
    }

    this.http.get<ReservaHistorialResponseDTO[]>(url)
      .subscribe({
        next: (data) => {
          this.historial = data;
          this.loadingHistorial = false;
        },
        error: (error) => {
          console.error('Error cargando historial:', error);
          this.loadingHistorial = false;
          // Datos de ejemplo
          this.historial = [
            {
              id: 3,
              alojamientoNombre: 'Cabaña en la Montaña',
              alojamientoImagen: 'https://images.unsplash.com/photo-1518780664697-55e3ad937233?w=400',
              fechaInicio: '2025-10-01',
              fechaFin: '2025-10-05',
              estado: 'COMPLETADA',
              precioTotal: 900000,
              fechaReserva: '2025-09-15'
            }
          ];
        }
      });
  }

  // ============================================
  // 🔹 Eliminar Favorito
  // ============================================
  removeFavorito(id: number, alojamientoId: number) {
    if (confirm('¿Estás seguro de eliminar este favorito?')) {
      this.http.delete(`${this.API_URL}/favoritos/${id}/usuario/${this.usuarioId}`)
        .subscribe({
          next: () => {
            this.favoritos = this.favoritos.filter(f => f.id !== id);
            console.log('Favorito eliminado');
          },
          error: (error) => {
            console.error('Error eliminando favorito:', error);
            // Simular eliminación en caso de error
            this.favoritos = this.favoritos.filter(f => f.id !== id);
          }
        });
    }
  }

  // ============================================
  // 🔹 Ver Detalle de Reserva
  // ============================================
  verDetalleReserva(id: number) {
    this.http.get<ReservaResponseDTO>(`${this.API_URL}/reservas/${id}`)
      .subscribe({
        next: (reserva) => {
          console.log('Detalle de reserva:', reserva);
          // Aquí podrías abrir un modal o navegar a otra página
        },
        error: (error) => {
          console.error('Error obteniendo detalle:', error);
        }
      });
  }

  // ============================================
  // 🔹 Helpers
  // ============================================
  getEstadoClass(estado: string): string {
    switch(estado.toLowerCase()) {
      case 'confirmada': return 'estado-confirmada';
      case 'pendiente': return 'estado-pendiente';
      case 'completada': return 'estado-completada';
      case 'cancelada': return 'estado-cancelada';
      default: return '';
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES', { day: '2-digit', month: 'short', year: 'numeric' });
  }
}
