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

import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { environment } from '../../environments/environment';

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
  nombreUsuario: string;
  emailUsuario: string;
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
  usuarioId: number;
  nombre: string;
  correo: string;
  telefono?: string;
  fechaNacimiento?: string;
  role: Role;
}

interface UpdateUserDTO {
  id: number;
  nombre: string;
  telefono: string;
  fechaNacimiento: string;
  contrasena: string;
  usuarioId: number;
}

interface CreateUserDTO {
  nombre: string;
  correo: string;
  contrasena: string;
  telefono?: string;
  fechaNacimiento?: string;
  usuarioId?: number;
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
  public readonly API_URL = `${environment.apiUrl}/api`;

  // Vista actual
  currentView: 'perfil' | 'solicitud-publicaciones' | 'solicitud-anfitriones' | 'asignar-rol' | 'listar-usuarios' | 'listar-por-rol' | 'crear-usuario' = 'perfil';

  // Admin actual
  adminId: number = 1;
  adminNombre: string = 'Admin Principal';
  adminCorreo: string = 'admin@stayfinder.com';

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
    correo: '',
    contrasena: '',
    telefono: '',
    fechaNacimiento: '',
    usuarioId: null as any
  };

  showEditProfileDialog: boolean = false;
  profileForm: UpdateUserDTO = {
    id: 0,
    nombre: '',
    telefono: '',
    fechaNacimiento: '',
    contrasena: '',
    usuarioId: 0
  };

  // Mobile sidebar state
  sidebarVisible: boolean = false;

  constructor(private http: HttpClient, private auth: AuthService, private router: Router) { }

  ngOnInit() {
    this.auth.currentUser$.subscribe(user => {
      if (user) {
        this.adminId = user.id || 1;
        this.adminNombre = user.nombre || 'Admin Principal';
        this.adminCorreo = user.email || 'admin@stayfinder.com';
      }
    });

    // Cargar los datos iniciales para el dashboard principal
    this.loadSolicitudesPublicacion();
    this.loadSolicitudesOwner();
    this.loadUsuarios();
  }

  // ============================================
  // 🔹 Editar Perfil
  // ============================================

  openEditProfileDialog() {
    this.auth.currentUser$.subscribe(user => {
      if (user) {
        // Carga los datos base desde el token JWT o los datos de la sesión guardados
        this.profileForm = {
          id: user.id || this.adminId, // Database PK
          nombre: user.nombre || this.adminNombre,
          telefono: '', // Inicialmente vacio hasta traer de DB, opcional refinar si guardan en cache
          fechaNacimiento: '',
          contrasena: '',     // Siempre en blanco hasta que el user quiera cambiarla
          usuarioId: user.usuarioId || this.adminId // Logical Id
        };

        // Hacemos una consulta GET extra para popular teléfono y fecha correcta
        this.http.get<UsuarioResponseDTO[]>(`${this.API_URL}/usuario`).subscribe(users => {
          const myself = users.find(u => u.usuarioId === this.adminId);
          if (myself) {
            this.profileForm.nombre = myself.nombre;
            this.profileForm.telefono = myself.telefono || '';
            this.profileForm.fechaNacimiento = myself.fechaNacimiento || '';
          }
          this.showEditProfileDialog = true;
        });
      }
    }).unsubscribe(); // nos desuscribimos de inmediato para un 'on-demand' snapshot
  }

  guardarPerfil() {
    this.http.put<UsuarioResponseDTO>(
      `${this.API_URL}/usuario/${this.adminId}`,
      this.profileForm
    ).subscribe({
      next: (data) => {
        alert('Perfil actualizado con éxito');
        this.adminNombre = data.nombre;
        this.showEditProfileDialog = false;
        // Refrescar auth token o requerir login nuevamente para JWT update? Temporalmente actualizar visual.
      },
      error: (err) => {
        console.error('Error al editar perfil', err);
        alert('Error editando perfil');
      }
    });
  }

  // ============================================
  // 🔹 Cambiar Vista
  // ============================================
  changeView(view: any) {
    this.currentView = view;
    this.sidebarVisible = false;

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

  irInicio() {
    this.router.navigate(['/']);
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
      adminId: this.adminId, // 🔹 Agregado el ID del administrador
      aprobada: aprobada,
      comentario: comentarioRespuesta // 🔹 Cambiado de comentarioRespuesta a comentario
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
          const backendMessage = error.error?.message || error.message || 'Error desconocido';
          alert('Hubo un error al responder la solicitud: ' + backendMessage);
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
      adminId: this.adminId,
      aprobada: aprobada,
      comentario: comentarioRespuesta
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
          const backendMessage = error.error?.message || error.message || 'Error desconocido';
          alert('Error del servidor: ' + backendMessage);
          this.showRespuestaDialog = false;
        }
      });
  }

  // ============================================
  // 🔹 Gestión de Usuarios
  // ============================================
  loadUsuarios() {
    this.loading = true;
    this.http.get<UsuarioResponseDTO[]>(`${this.API_URL}/usuario`)
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
              usuarioId: 1000000001,
              nombre: 'Admin Principal',
              correo: 'admin@stayfinder.com',
              role: Role.ADMIN
            },
            {
              id: 2,
              usuarioId: 1000000002,
              nombre: 'Juan Pérez',
              correo: 'juan@example.com',
              role: Role.CLIENT,
              telefono: '3001234567'
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
    this.http.get<UsuarioResponseDTO[]>(`${this.API_URL}/usuario/role/${this.selectedRoleFiltro}`)
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
      `${this.API_URL}/usuario/${this.selectedUsuario.usuarioId}/role?newRole=${this.selectedRole}&adminUsuarioId=${this.adminId}`,
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
      `${this.API_URL}/usuario?role=${this.selectedRole}&adminUsuarioId=${this.adminId}`,
      this.nuevoUsuario
    ).subscribe({
      next: (response) => {
        console.log('Usuario creado:', response);
        alert('Usuario creado exitosamente');
        this.nuevoUsuario = { nombre: '', correo: '', contrasena: '', telefono: '', fechaNacimiento: '', usuarioId: null as any };
        this.selectedRole = Role.CLIENT;
      },
      error: (error) => {
        console.error('Error creando usuario en la BD:', error);
        alert('Ocurrió un error al crear el usuario. Por favor verifica tus permisos y la consola.');
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
