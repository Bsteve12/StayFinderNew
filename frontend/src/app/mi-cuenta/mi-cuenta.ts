import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { MessageService } from 'primeng/api';

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
  standalone: true, // Asegúrate de tenerlo si usas imports directos
  imports: [CommonModule, HttpClientModule, ButtonModule, CardModule],
  templateUrl: './mi-cuenta.html',
  styleUrl: './mi-cuenta.scss',
})
export class MiCuenta implements OnInit {
  private readonly API_URL = 'http://localhost:8080/api';

  // Estado de la vista actual
  currentView: 'profile' | 'reservas' | 'favoritos' | 'historial' | 'solicitudes' = 'profile';

  // Usuario actual - AHORA DINÁMICO
  usuarioId: number = 1;
  usuarioNombre: string = 'Juan Pérez';
  usuarioEmail: string = 'juan.perez@example.com';
  imagenPerfil: string | null = null; // 👈 NUEVO CAMPO PARA LA IMAGEN

  // Datos
  reservas: ReservaResponseDTO[] = [];
  favoritos: FavoriteResponseDTO[] = [];
  historial: ReservaHistorialResponseDTO[] = [];

  // Loading states
  loadingReservas: boolean = false;
  loadingFavoritos: boolean = false;
  loadingHistorial: boolean = false;

  // Mobile Sidebar
  sidebarVisible: boolean = false;

  // Obtenemos la URL base del backend desde environment si queremos servir las imágenes,
  // O podemos usar un Pipe de Angular. Lo importaremos de environment:
  private readonly baseUrl = 'http://localhost:8080';

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
        this.usuarioId = user.id || 1;
        this.usuarioNombre = user.nombre || 'Cargando...';
        this.usuarioEmail = user.email || '';

        // La imagen que viene en el token puede ser nula o la ruta parcial
        if (user.imagenPerfil) {
          this.imagenPerfil = this.baseUrl + user.imagenPerfil;
        } else {
          this.imagenPerfil = null;
        }

        // 🔹 Llamada al backend para cercioranos de la foto más reciente
        this.authService.fetchUsuarioDetalle(this.usuarioId).subscribe({
          next: (detalle) => {
            if (detalle.imagenPerfil) {
              this.imagenPerfil = this.baseUrl + detalle.imagenPerfil;
            }
          }
        });

        this.loadReservas(); // Carga inicial con el ID correcto
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
    }
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

    // El token pasará automáticamente por el interceptor, o si no, el controller `/api/reservas` obtiene 
    // las reservas del usuario autenticado actual.
    this.http.get<ReservaResponseDTO[]>(`${this.API_URL}/reservas`).subscribe({
      next: (data) => {
        this.reservas = data;
        this.loadingReservas = false;
      },
      error: (error) => {
        console.error('Error cargando reservas:', error);
        this.loadingReservas = false;
        // Si quieres, podrías dejar el arreglo vacío o mostrar mensaje
        this.reservas = [];
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
          // Datos de ejemplo en caso de error para no romper la UI
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

    let url = `${this.API_URL}/historial/usuario/${this.usuarioId}`;
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