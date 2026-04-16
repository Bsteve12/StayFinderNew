import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { Observable } from 'rxjs';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { MessageService } from 'primeng/api';
import { environment } from '../../environments/environment';

// Interfaces
interface ReservaResponseDTO {
  id: number;
  alojamientoId?: number;
  alojamientoNombre: string;
  alojamientoImagen?: string;
  fechaInicio: string;
  fechaFin: string;
  estado: string;
  precioTotal: number;
  tipoReserva?: string;
  numeroHuespedes?: number;
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
  reservaId: number;
  id?: number;
  alojamientoId: number;
  alojamientoNombre: string;
  alojamientoImagen: string;
  usuarioId: number;
  usuarioNombre: string;
  fechaInicio: string;
  fechaFin: string;
  numeroHuespedes: number;
  precioTotal: number;
  fechaReserva: string;
  estado: string;
  tipoReserva: string;
}

@Component({
  selector: 'app-mi-cuenta',
  standalone: true, // Asegúrate de tenerlo si usas imports directos
  imports: [CommonModule, HttpClientModule, ButtonModule, CardModule, RouterLink],
  templateUrl: './mi-cuenta.html',
  styleUrl: './mi-cuenta.scss',
})
export class MiCuenta implements OnInit {
  private readonly API_URL = `${environment.apiUrl}/api`;

  // Estado de la vista actual
  currentView: 'profile' | 'reservas' | 'favoritos' | 'historial' | 'solicitudes' = 'profile';

  // Usuario actual - AHORA DINÁMICO
  usuarioId: number = 0; // ID de BD (Primary Key)
  documentoId: number = 0; // Cédula (Documento)
  usuarioNombre: string = 'Usuario';
  usuarioEmail: string = '';
  imagenPerfil: string | null = null;

  // Datos
  reservas: ReservaResponseDTO[] = [];
  favoritos: FavoriteResponseDTO[] = [];
  historial: ReservaHistorialResponseDTO[] = [];
  solicitudes: any[] = []; // NUEVO

  // Loading states
  loadingReservas: boolean = false;
  loadingFavoritos: boolean = false;
  loadingHistorial: boolean = false;
  loadingSolicitudes: boolean = false; // NUEVO

  // Mobile Sidebar
  sidebarVisible: boolean = false;

  // Obtenemos la URL base del backend desde environment si queremos servir las imágenes,
  // O podemos usar un Pipe de Angular. Lo importaremos de environment:
  private readonly baseUrl = environment.apiUrl;

  // Inyectamos AuthService para obtener el usuario real
  constructor(
    private http: HttpClient,
    private router: Router,
    private authService: AuthService,
    private messageService: MessageService
  ) { }

  ngOnInit() {
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.usuarioId = user.id || 0; // Ahora es el ID Auténtico de BD (PK)
        this.documentoId = user.usuarioId || 0; // Mantenemos el documento (Cédula) por si se necesita
        this.usuarioNombre = user.nombre || 'Usuario';

        if (user.imagenPerfil) {
          this.imagenPerfil = this.baseUrl + user.imagenPerfil;
        } else {
          this.imagenPerfil = null;
        }

        this.authService.fetchUsuarioDetalle(this.usuarioId).subscribe({
          next: (detalle) => {
            if (detalle.imagenPerfil) {
              this.imagenPerfil = this.baseUrl + detalle.imagenPerfil;
            }
          }
        });

        // Solo cargar datos si tenemos un ID válido
        if (this.usuarioId > 0) {
          this.loadReservas();
          this.loadFavoritos();
          this.loadHistorial();
        }
      } else {
        // No autenticado
        this.loadingReservas = false;
      }
    });
  }

  // ============================================
  // 🔹 Subida de Foto de Perfil
  // ============================================
  triggerFileInput() {
    const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
    if (fileInput) {
      fileInput.click();
    }
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.authService.uploadProfileImage(this.usuarioId, file).subscribe({
        next: (response) => {
          console.log('Imagen subida', response);
          this.imagenPerfil = this.baseUrl + response.imagenPerfil;
        },
        error: (err) => {
          console.error('Error al subir la imagen', err);
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error al subir la foto de perfil' });
        }
      });
    }
  }

  // ============================================
  // 🔹 Cambiar Vista
  // ============================================
  changeView(view: 'profile' | 'reservas' | 'favoritos' | 'solicitudes' | 'historial') {
    this.currentView = view;
    this.sidebarVisible = false;

    switch (view) {
      case 'profile':
        // Ya está cargado
        break;
      case 'reservas':
        this.loadReservas();
        break;
      case 'favoritos':
        this.loadFavoritos();
        break;
      case 'historial':
        this.loadHistorial();
        break;
      case 'solicitudes':
        this.loadSolicitudes();
        break;
    }
  }

  // ============================================
  // 🔹 Cargar Solicitudes Anfitrión
  // ============================================
  loadSolicitudes() {
    if (this.loadingSolicitudes) return;
    this.loadingSolicitudes = true;
    this.http.get<any[]>(`${this.API_URL}/solicitudes-owner/usuario/${this.usuarioId}`).subscribe({
      next: (data) => {
        console.log(`[DEBUG] Solicitudes recibidas para ID ${this.usuarioId}:`, data);
        this.solicitudes = data;
        this.loadingSolicitudes = false;
      },
      error: (err) => {
        console.error('Error cargando solicitudes:', err);
        this.solicitudes = [];
        this.loadingSolicitudes = false;
      }
    });
  }

  // ============================================
  // 🔹 SIDEBAR TOGGLE
  // ============================================
  toggleSidebar() {
    this.sidebarVisible = !this.sidebarVisible;
  }

  irInicio() {
    this.router.navigate(['/']);
  }

  // ============================================
  // 🔹 Cargar Reservas Activas
  // ============================================
  loadReservas() {
    if (this.loadingReservas) return;

    this.loadingReservas = true;

    this.http.get<any[]>(`${this.API_URL}/historial/usuario/${this.usuarioId}`).subscribe({
      next: (data) => {
        // Filtrar solo las activas (PENDIENTE o CONFIRMADA) y mapear los campos
        this.reservas = data
          .filter(r => r.estado === 'PENDIENTE' || r.estado === 'CONFIRMADA')
          .map(r => ({
            id: r.reservaId || r.id,
            alojamientoId: r.alojamientoId,
            alojamientoNombre: r.alojamientoNombre || 'Sin nombre',
            alojamientoImagen: r.alojamientoImagen,
            fechaInicio: r.fechaInicio,
            fechaFin: r.fechaFin,
            estado: r.estado,
            precioTotal: r.precioTotal,
            tipoReserva: r.tipoReserva,
            numeroHuespedes: r.numeroHuespedes
          }));
        this.loadingReservas = false;
      },
      error: (error) => {
        console.error('Error cargando reservas:', error);
        this.loadingReservas = false;
        this.reservas = [];
      }
    });
  }

  // ============================================
  // 🔹 Cancelar Reserva (Cliente)
  // ============================================
  cancelarReserva(id: number) {
    if (!confirm('¿Estás seguro de que quieres cancelar esta reserva?')) return;

    const payload = { reservaId: id, motivo: 'Cancelado por el cliente' };
    this.http.patch(`${this.API_URL}/reservas/${id}/cancelar`, payload).subscribe({
      next: () => {
        this.messageService.add({ severity: 'info', summary: 'Cancelada', detail: 'Tu reserva ha sido cancelada exitosamente.' });
        this.loadReservas();
      },
      error: (err) => {
        console.error('Error cancelando reserva:', err);
        const msg = err.error?.message || err.error || 'No se pudo cancelar la reserva.';
        this.messageService.add({ severity: 'error', summary: 'Error', detail: msg });
      }
    });
  }

  // ============================================
  // 🔹 Cargar Favoritos
  // ============================================
  loadFavoritos() {
    if (this.loadingFavoritos) return;

    this.loadingFavoritos = true;
    // Aquí usamos el usuarioId dinámico que viene del Auth
    this.http.get<FavoriteResponseDTO[]>(`${this.API_URL}/favoritos/usuario/${this.usuarioId}`)
      .subscribe({
        next: (data) => {
          this.favoritos = data;
          this.loadingFavoritos = false;
        },
        error: (error) => {
          console.error('Error cargando favoritos:', error);
          this.loadingFavoritos = false;
          this.favoritos = [];
        }
      });
  }

  // ============================================
  // 🔹 Cargar Historial
  // ============================================
  loadHistorial(filtros?: HistorialReservasRequestDTO) {
    if (this.loadingHistorial) return;
    this.loadingHistorial = true;

    let url = `${this.API_URL}/historial/usuario/${this.usuarioId}`;
    const params: string[] = [];
    if (filtros?.fechaInicio) params.push(`fechaInicio=${filtros.fechaInicio}`);
    if (filtros?.fechaFin) params.push(`fechaFin=${filtros.fechaFin}`);
    if (filtros?.estado) params.push(`estado=${filtros.estado}`);
    if (params.length > 0) url += '?' + params.join('&');

    this.http.get<ReservaHistorialResponseDTO[]>(url)
      .subscribe({
        next: (data) => {
          // El endpoint /historial devuelve ReservaHistorialResponseDTO
          // con campo 'reservaId' en lugar de 'id'
          this.historial = data.map(r => ({
            ...r,
            id: (r as any).reservaId || (r as any).id || 0
          })) as any;
          this.loadingHistorial = false;
        },
        error: (error) => {
          console.error('Error cargando historial:', error);
          this.loadingHistorial = false;
          this.historial = [];
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
    switch (estado.toLowerCase()) {
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