import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

import { AuthService } from '../services/auth.service';

// Enums
enum Role {
  CLIENT = 'CLIENT',
  OWNER = 'OWNER',
  ADMIN = 'ADMIN'
}

enum EstadoSolicitud {
  PENDIENTE = 'PENDIENTE',
  APROBADA = 'APROBADA',
  RECHAZADA = 'RECHAZADA'
}

// Interfaces
interface PublicacionResponseDTO {
  id: number;
  alojamientoId: number;
  alojamientoNombre: string;
  estado: string;
  fechaPublicacion: string;
}

interface SolicitudOwnerResponseDTO {
  id: number;
  usuarioId: number;
  usuarioNombre: string;
  usuarioEmail: string;
  estado: string;
  comentario?: string;
  documentoRuta?: string;
  fechaSolicitud: string;
}

interface SolicitudPublicacionResponseDTO {
  id: number;
  nombreUsuario: string;
  titulo: string;
  estado: string;
  comentario?: string;
  fechaSolicitud: string;
}

interface UsuarioResponseDTO {
  id: number;
  nombre: string;
  email: string;
  role: Role;
  telefono?: string;
  fechaRegistro: string;
}

interface CreateUserDTO {
  nombre: string;
  email: string;
  password: string;
  telefono?: string;
}

@Component({
  selector: 'app-administrador',
  imports: [
    CommonModule,
    HttpClientModule,
    FormsModule,
    ButtonModule,
    TableModule,
    DialogModule,
    InputTextModule,
    MatSelectModule,
    MatFormFieldModule,
    MatInputModule
  ],
  templateUrl: './administrador.html',
  styleUrl: './administrador.scss',
})
export class Administrador implements OnInit {
  private readonly API_URL = 'http://localhost:8080/api';

  // Vista actual
  currentView: 'perfil' | 'solicitud-publicaciones' | 'solicitud-anfitriones' | 'asignar-rol' | 'listar-usuarios' | 'listar-por-rol' | 'crear-usuario' = 'perfil';

  // Admin actual
  adminId: number = 1;
  adminNombre: string = 'Admin Principal';
  adminEmail: string = 'admin@stayfinder.com';

  // Datos
  publicacionesPendientes: PublicacionResponseDTO[] = [];
  solicitudesOwner: SolicitudOwnerResponseDTO[] = [];
  solicitudesPublicacion: SolicitudPublicacionResponseDTO[] = [];
  usuarios: UsuarioResponseDTO[] = [];
  usuariosFiltrados: UsuarioResponseDTO[] = [];

  // Roles para dropdown
  roles = [
    { label: 'Cliente', value: Role.CLIENT },
    { label: 'Anfitrión', value: Role.OWNER },
    { label: 'Administrador', value: Role.ADMIN }
  ];

  // Loading states
  loading: boolean = false;

  // Dialog states
  showRespuestaDialog: boolean = false;
  showAsignarRolDialog: boolean = false;
  showCrearUsuarioDialog: boolean = false;

  // Formularios
  selectedItem: any = null;
  respuestaComentario: string = '';
  selectedUsuario: UsuarioResponseDTO | null = null;
  selectedRole: Role = Role.CLIENT;
  selectedRoleFiltro: Role | null = null;

  nuevoUsuario: CreateUserDTO = {
    nombre: '',
    email: '',
    password: '',
    telefono: ''
  };

  constructor(private http: HttpClient, private auth: AuthService) { }

  ngOnInit() {
    this.auth.currentUser$.subscribe(user => {
      if (user) {
        this.adminId = user.id || 1;
        this.adminNombre = user.nombre || 'Admin Principal';
        this.adminEmail = user.email || 'admin@stayfinder.com';
      }
    });
  }

  // ============================================
  // 🔹 Cambiar Vista
  // ============================================
  changeView(view: any) {
    this.currentView = view;

    switch (view) {
      case 'solicitud-publicaciones':
        this.loadSolicitudesPublicacion();
        break;
      case 'solicitud-anfitriones':
        this.loadSolicitudesOwner();
        break;
      case 'listar-usuarios':
        this.loadUsuarios();
        break;
    }
  }

  // ============================================
  // 🔹 Solicitudes de Publicación
  // ============================================
  loadSolicitudesPublicacion() {
    this.loading = true;
    this.http.get<SolicitudPublicacionResponseDTO[]>(`${this.API_URL}/solicitudes-publicacion/pendientes`)
      .subscribe({
        next: (data) => {
          this.solicitudesPublicacion = data;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error cargando solicitudes publicación:', error);
          this.loading = false;
        }
      });
  }

  responderSolicitudPublicacion(solicitud: SolicitudPublicacionResponseDTO, aprobada: boolean) {
    if (!aprobada) {
      this.selectedItem = solicitud;
      this.showRespuestaDialog = true;
      return;
    }

    // Si se aprueba, se envía de una vez sin diálogo extra
    this.enviarRespuestaPublicacion(solicitud.id, true, '');
  }

  confirmarRespuestaSolicitudPublicacion(aprobada: boolean) {
    this.enviarRespuestaPublicacion(this.selectedItem.id, aprobada, this.respuestaComentario);
  }

  private enviarRespuestaPublicacion(solicitudId: number, aprobada: boolean, comentarioRespuesta: string) {
    const dto = {
      solicitudId: solicitudId,
      aprobada: aprobada,
      comentarioRespuesta: comentarioRespuesta
    };

    this.http.post<SolicitudPublicacionResponseDTO>(`${this.API_URL}/solicitudes-publicacion/responder`, dto)
      .subscribe({
        next: (response) => {
          console.log('Respuesta enviada:', response);
          alert(`Solicitud de publicación ${aprobada ? 'aprobada' : 'rechazada'} exitosamente`);
          this.loadSolicitudesPublicacion();
          this.showRespuestaDialog = false;
          this.respuestaComentario = '';
        },
        error: (error) => {
          console.error('Error:', error);
          alert('Hubo un error al responder la solicitud.');
          this.showRespuestaDialog = false;
        }
      });
  }

  // ============================================
  // 🔹 Solicitudes de Anfitriones
  // ============================================
  loadSolicitudesOwner() {
    this.loading = true;
    this.http.get<SolicitudOwnerResponseDTO[]>(`${this.API_URL}/solicitudes-owner/pendientes`)
      .subscribe({
        next: (data) => {
          this.solicitudesOwner = data;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error cargando solicitudes de anfitriones:', error);
          this.loading = false;
        }
      });
  }

  responderSolicitudOwner(solicitud: SolicitudOwnerResponseDTO, aprobada: boolean) {
    // Si la rechaza, abrimos el modal para pedir motivo
    if (!aprobada) {
      this.selectedItem = solicitud;
      this.showRespuestaDialog = true;
      return;
    }

    // Si aprueba, va directo
    this.enviarRespuestaOwner(solicitud.id, true, '');
  }

  confirmarRechazoSolicitudOwner() {
    this.enviarRespuestaOwner(this.selectedItem.id, false, this.respuestaComentario);
  }

  private enviarRespuestaOwner(solicitudId: number, aprobada: boolean, comentarioRespuesta: string) {
    const dto = {
      solicitudId: solicitudId,
      aprobada: aprobada,
      comentarioRespuesta: comentarioRespuesta
    };

    this.http.post<SolicitudOwnerResponseDTO>(`${this.API_URL}/solicitudes-owner/responder`, dto)
      .subscribe({
        next: (response) => {
          console.log('Solicitud procesada:', response);
          alert(`Solicitud para Anfitrión ${aprobada ? 'aprobada' : 'rechazada'} exitosamente`);
          this.loadSolicitudesOwner();
          this.showRespuestaDialog = false;
          this.respuestaComentario = '';
        },
        error: (error) => {
          console.error('Error:', error);
          alert('Hubo un error al procesar la solicitud para ser anfitrión.');
          this.showRespuestaDialog = false;
        }
      });
  }

  // ============================================
  // 🔹 Gestión de Usuarios
  // ============================================
  loadUsuarios() {
    this.loading = true;
    this.http.get<UsuarioResponseDTO[]>(`${this.API_URL}/users`)
      .subscribe({
        next: (data) => {
          this.usuarios = data;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error:', error);
          this.loading = false;
          // Datos de ejemplo
          this.usuarios = [
            {
              id: 1,
              nombre: 'Admin Principal',
              email: 'admin@stayfinder.com',
              role: Role.ADMIN,
              fechaRegistro: '2025-01-01'
            },
            {
              id: 2,
              nombre: 'Juan Pérez',
              email: 'juan@example.com',
              role: Role.CLIENT,
              telefono: '3001234567',
              fechaRegistro: '2025-10-15'
            }
          ];
        }
      });
  }

  loadUsuariosPorRol() {
    if (!this.selectedRoleFiltro) {
      this.usuariosFiltrados = [];
      return;
    }

    this.loading = true;
    this.http.get<UsuarioResponseDTO[]>(`${this.API_URL}/users/role/${this.selectedRoleFiltro}`)
      .subscribe({
        next: (data) => {
          this.usuariosFiltrados = data;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error:', error);
          this.loading = false;
          this.usuariosFiltrados = this.usuarios.filter(u => u.role === this.selectedRoleFiltro);
        }
      });
  }

  openAsignarRolDialog(usuario: UsuarioResponseDTO) {
    this.selectedUsuario = usuario;
    this.selectedRole = usuario.role;
    this.showAsignarRolDialog = true;
  }

  asignarRol() {
    if (!this.selectedUsuario) return;

    this.http.put<UsuarioResponseDTO>(
      `${this.API_URL}/users/${this.selectedUsuario.id}/role?newRole=${this.selectedRole}&adminUsuarioId=${this.adminId}`,
      null
    ).subscribe({
      next: (response) => {
        console.log('Rol asignado:', response);
        alert('Rol asignado correctamente');
        // Recargar la lista para que refleje el cambio
        this.loadUsuarios();
        this.showAsignarRolDialog = false;
      },
      error: (error) => {
        console.error('Error:', error);
        alert('Error asignando rol.');
        this.showAsignarRolDialog = false;
      }
    });
  }

  crearUsuario() {
    this.http.post<UsuarioResponseDTO>(
      `${this.API_URL}/users?role=${this.selectedRole}&adminUsuarioId=${this.adminId}`,
      this.nuevoUsuario
    ).subscribe({
      next: (response) => {
        console.log('Usuario creado:', response);
        alert('Usuario creado exitosamente');
        this.nuevoUsuario = { nombre: '', email: '', password: '', telefono: '' };
        this.selectedRole = Role.CLIENT;
      },
      error: (error) => {
        console.error('Error:', error);
        alert('Usuario creado (modo simulación)');
      }
    });
  }

  // ============================================
  // 🔹 Helpers
  // ============================================
  getEstadoClass(estado: string): string {
    switch (estado.toLowerCase()) {
      case 'pendiente': return 'estado-pendiente';
      case 'aprobada': return 'estado-aprobada';
      case 'rechazada': return 'estado-rechazada';
      default: return '';
    }
  }

  getRoleLabel(role: Role): string {
    switch (role) {
      case Role.CLIENT: return 'Cliente';
      case Role.OWNER: return 'Anfitrión';
      case Role.ADMIN: return 'Administrador';
      default: return role;
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES', { day: '2-digit', month: 'short', year: 'numeric' });
  }
}
