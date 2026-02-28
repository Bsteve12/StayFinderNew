import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { TextareaModule } from 'primeng/textarea';
import { Observable } from 'rxjs';

// Interfaces
interface AlojamientoRequestDTO {
  nombre: string;
  descripcion: string;
  direccion: string;
  precio: number;
  capacidadMaxima: number;
  imagenes?: any[];
  servicios?: string[];
}

interface AlojamientoResponseDTO {
  id: number;
  nombre: string;
  descripcion: string;
  direccion: string;
  precio: number;
  capacidadMaxima: number;
  imagenes: any[];
  servicios: string[];
  estado?: string;
  ownerId: number;
}

interface SolicitudPublicacionRequestDTO {
  alojamientoId: number;
  comentarios?: string;
}

interface SolicitudPublicacionResponseDTO {
  id: number;
  alojamientoId: number;
  alojamientoNombre: string;
  estado: string;
  fechaSolicitud: string;
  comentarios?: string;
}

interface ReservaHistorialResponseDTO {
  id: number;
  alojamientoNombre: string;
  alojamientoImagen: string;
  usuarioNombre: string;
  fechaInicio: string;
  fechaFin: string;
  estado: string;
  precioTotal: number;
  fechaReserva: string;
}

@Component({
  selector: 'app-anfitrion',
  imports: [
    CommonModule,
    HttpClientModule,
    FormsModule,
    ButtonModule,
    CardModule,
    DialogModule,
    InputTextModule,
    TextareaModule
  ],
  templateUrl: './anfitrion.html',
  styleUrls: ['./anfitrion.scss'],
})
export class Anfitrion implements OnInit {
  private readonly API_URL = 'http://localhost:8080/api';

  // Estado de la vista actual
  currentView: 'perfil' | 'historial' | 'servicios' | 'gestion' | 'calendario' | 'solicitar' = 'perfil';

  // Owner actual (simulado - debería venir del servicio de autenticación)
  ownerId: number = 1;
  ownerNombre: string = 'María García';
  ownerEmail: string = 'maria.garcia@example.com';

  // Datos
  alojamientos: AlojamientoResponseDTO[] = [];
  historialReservas: ReservaHistorialResponseDTO[] = [];
  solicitudes: SolicitudPublicacionResponseDTO[] = [];

  // Loading states
  loadingAlojamientos: boolean = false;
  loadingHistorial: boolean = false;

  // Dialog states
  showCreateDialog: boolean = false;
  showEditDialog: boolean = false;
  showSolicitudDialog: boolean = false;

  // Formulario
  alojamientoForm: AlojamientoRequestDTO = this.getEmptyForm();
  selectedAlojamiento: AlojamientoResponseDTO | null = null;

  constructor(private http: HttpClient) { }

  ngOnInit() {
    this.loadAlojamientos();
  }

  // ============================================
  // 🔹 Cambiar Vista
  // ============================================
  changeView(view: 'perfil' | 'historial' | 'servicios' | 'gestion' | 'calendario' | 'solicitar') {
    this.currentView = view;

    switch (view) {
      case 'gestion':
        this.loadAlojamientos();
        break;
      case 'historial':
        this.loadHistorialReservas();
        break;
    }
  }

  // ============================================
  // 🔹 Gestión de Alojamientos
  // ============================================
  loadAlojamientos() {
    if (this.loadingAlojamientos) return;

    this.loadingAlojamientos = true;
    this.http.get<AlojamientoResponseDTO[]>(`${this.API_URL}/alojamientos/owner/${this.ownerId}`)
      .subscribe({
        next: (data) => {
          this.alojamientos = data.map(a => ({
            ...a,
            estado: a.estado || 'BORRADOR' // TODO: Leer de la publicación real
          }));
          this.loadingAlojamientos = false;
        },
        error: (error) => {
          console.error('Error cargando alojamientos:', error);
          this.loadingAlojamientos = false;
        }
      });
  }

  // ============================================
  // 🔹 Crear Alojamiento
  // ============================================
  openCreateDialog() {
    this.alojamientoForm = this.getEmptyForm();
    this.showCreateDialog = true;
  }

  crearAlojamiento() {
    this.http.post<AlojamientoResponseDTO>(
      `${this.API_URL}/alojamientos?ownerId=${this.ownerId}`,
      this.alojamientoForm
    ).subscribe({
      next: (response) => {
        console.log('Alojamiento creado:', response);
        this.alojamientos.push(response);
        this.showCreateDialog = false;
        this.alojamientoForm = this.getEmptyForm();
      },
      error: (error) => {
        console.error('Error creando alojamiento:', error);
        // Simulación en caso de error
        const newAlojamiento: AlojamientoResponseDTO = {
          id: Date.now(),
          ...this.alojamientoForm,
          imagenes: this.alojamientoForm.imagenes || [],
          servicios: this.alojamientoForm.servicios || [],
          estado: 'BORRADOR',
          ownerId: this.ownerId
        };
        this.alojamientos.push(newAlojamiento);
        this.showCreateDialog = false;
      }
    });
  }

  // ============================================
  // 🔹 Editar Alojamiento
  // ============================================
  openEditDialog(alojamiento: AlojamientoResponseDTO) {
    this.selectedAlojamiento = alojamiento;
    this.alojamientoForm = {
      nombre: alojamiento.nombre,
      descripcion: alojamiento.descripcion,
      direccion: alojamiento.direccion,
      precio: alojamiento.precio,
      capacidadMaxima: alojamiento.capacidadMaxima,
      imagenes: alojamiento.imagenes,
      servicios: alojamiento.servicios
    };
    this.showEditDialog = true;
  }

  editarAlojamiento() {
    if (!this.selectedAlojamiento) return;

    this.http.put<AlojamientoResponseDTO>(
      `${this.API_URL}/alojamientos/${this.selectedAlojamiento.id}?ownerId=${this.ownerId}`,
      this.alojamientoForm
    ).subscribe({
      next: (response) => {
        console.log('Alojamiento editado:', response);
        const index = this.alojamientos.findIndex(a => a.id === this.selectedAlojamiento!.id);
        if (index !== -1) {
          this.alojamientos[index] = response;
        }
        this.showEditDialog = false;
        this.selectedAlojamiento = null;
      },
      error: (error) => {
        console.error('Error editando alojamiento:', error);
        // Simulación
        const index = this.alojamientos.findIndex(a => a.id === this.selectedAlojamiento!.id);
        if (index !== -1) {
          this.alojamientos[index] = { ...this.alojamientos[index], ...this.alojamientoForm };
        }
        this.showEditDialog = false;
      }
    });
  }

  // ============================================
  // 🔹 Eliminar Alojamiento
  // ============================================
  eliminarAlojamiento(id: number) {
    if (!confirm('¿Estás seguro de eliminar este alojamiento?')) return;

    this.http.delete(
      `${this.API_URL}/alojamientos/${id}?ownerId=${this.ownerId}`
    ).subscribe({
      next: () => {
        console.log('Alojamiento eliminado');
        this.alojamientos = this.alojamientos.filter(a => a.id !== id);
      },
      error: (error) => {
        console.error('Error eliminando alojamiento:', error);
        // Simulación
        this.alojamientos = this.alojamientos.filter(a => a.id !== id);
      }
    });
  }

  // ============================================
  // 🔹 Solicitar Publicación
  // ============================================
  openSolicitudDialog(alojamiento: AlojamientoResponseDTO) {
    this.selectedAlojamiento = alojamiento;
    this.showSolicitudDialog = true;
  }

  solicitarPublicacion(comentarios?: string) {
    if (!this.selectedAlojamiento) return;

    const solicitud: SolicitudPublicacionRequestDTO = {
      alojamientoId: this.selectedAlojamiento.id,
      comentarios: comentarios
    };

    this.http.post<SolicitudPublicacionResponseDTO>(
      `${this.API_URL}/solicitudes-publicacion`,
      solicitud
    ).subscribe({
      next: (response) => {
        console.log('Solicitud creada:', response);
        alert('Solicitud de publicación enviada correctamente');
        this.showSolicitudDialog = false;
        this.selectedAlojamiento = null;
      },
      error: (error) => {
        console.error('Error creando solicitud:', error);
        alert('Solicitud enviada (modo simulación)');
        this.showSolicitudDialog = false;
      }
    });
  }

  // ============================================
  // 🔹 Historial de Reservas
  // ============================================
  loadHistorialReservas(filtros?: any) {
    if (this.loadingHistorial) return;

    this.loadingHistorial = true;

    let url = `${this.API_URL}/reservas/anfitrion/${this.ownerId}`;
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
          this.historialReservas = data;
          this.loadingHistorial = false;
        },
        error: (error) => {
          console.error('Error cargando historial:', error);
          this.loadingHistorial = false;
          // Datos de ejemplo
          this.historialReservas = [
            {
              id: 1,
              alojamientoNombre: 'Casa en la Playa',
              alojamientoImagen: 'https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?w=400',
              usuarioNombre: 'Juan Pérez',
              fechaInicio: '2025-11-20',
              fechaFin: '2025-11-25',
              estado: 'CONFIRMADA',
              precioTotal: 750000,
              fechaReserva: '2025-11-01'
            }
          ];
        }
      });
  }

  // ============================================
  // 🔹 Helpers
  // ============================================
  getEmptyForm(): AlojamientoRequestDTO {
    return {
      nombre: '',
      descripcion: '',
      direccion: '',
      precio: 0,
      capacidadMaxima: 1,
      imagenes: [],
      servicios: []
    };
  }

  getEstadoClass(estado: string): string {
    switch (estado.toLowerCase()) {
      case 'publicado': return 'estado-publicado';
      case 'pendiente': return 'estado-pendiente';
      case 'borrador': return 'estado-borrador';
      case 'rechazado': return 'estado-rechazado';
      default: return '';
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES', { day: '2-digit', month: 'short', year: 'numeric' });
  }
}
