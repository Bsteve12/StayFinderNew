import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { FloatLabelModule } from 'primeng/floatlabel';
import { TextareaModule } from 'primeng/textarea';
import { CarouselModule } from 'primeng/carousel';
import { MultiSelectModule } from 'primeng/multiselect';
import { ChartModule } from 'primeng/chart';
import { DatePickerModule } from 'primeng/datepicker';
import { SelectModule } from 'primeng/select';
import { MessageService } from 'primeng/api';
import { Observable, forkJoin } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { environment } from '../../environments/environment';

// Interfaces
interface AlojamientoRequestDTO {
  nombre: string;
  descripcion: string;
  direccion: string;
  precio: number;
  capacidadMaxima: number;
  serviciosIds?: number[];
}

interface AlojamientoResponseDTO {
  id: number;
  nombre: string;
  descripcion: string;
  direccion: string;
  precio: number;
  capacidadMaxima: number;
  estado?: string;
  ownerId: number;
  imagenes?: { id: number; url: string; alojamientoId: number }[];
  imagenesCompletas?: any[]; // Array for resolved actual URLs
  servicios?: ServicioResponseDTO[];
}

interface SolicitudPublicacionRequestDTO {
  usuarioId: number;
  alojamientoId: number;
  comentario?: string;
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
  reservaId: number;
  id?: number;
  alojamientoId?: number;
  alojamientoNombre: string;
  alojamientoImagen: string;
  usuarioNombre: string;
  usuarioId?: number;
  fechaInicio: string;
  fechaFin: string;
  estado: string;
  precioTotal: number;
  fechaReserva: string;
  tipoReserva?: string;
  numeroHuespedes?: number;
}

interface ServicioRequestDTO {
  nombre: string;
  descripcion: string;
  precio: number;
}

interface ServicioResponseDTO {
  id: number;
  nombre: string;
  descripcion: string;
  precio: number;
}

interface UpdateUserDTO {
  id: number;
  nombre: string;
  telefono: string;
  fechaNacimiento: string;
  contrasena: string;
  usuarioId: number;
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
    FloatLabelModule,
    TextareaModule,
    CarouselModule,
    MultiSelectModule,
    ChartModule,
    DatePickerModule,
    SelectModule
  ],
  templateUrl: './anfitrion.html',
  styleUrls: ['./anfitrion.scss'],
  encapsulation: ViewEncapsulation.None
})
export class Anfitrion implements OnInit {
  private readonly API_URL = `${environment.apiUrl}/api`;

  // Estado de la vista actual
  currentView: 'perfil' | 'historial' | 'servicios' | 'gestion' | 'calendario' | 'solicitar' | 'metricas' = 'perfil';

  // Owner actual (simulado - debería venir del servicio de autenticación)
  ownerId: number = 1;
  ownerNombre: string = 'María García';
  ownerEmail: string = 'maria.garcia@example.com';
  imagenPerfil: string | null = null; // 👈 NUEVO CAMPO PARA LA IMAGEN

  // Datos
  alojamientos: AlojamientoResponseDTO[] = [];
  historialReservas: ReservaHistorialResponseDTO[] = [];
  solicitudes: SolicitudPublicacionResponseDTO[] = [];

  // Loading states
  loadingAlojamientos: boolean = false;
  loadingHistorial: boolean = false;
  loadingServicios: boolean = false;
  loadingMetrics: boolean = false;

  // Datos para Métricas (Chart.js)
  totalAlojamientosMetric: number = 0;
  totalReservasMetric: number = 0;

  barChartData: any;
  pieChartData: any;
  chartOptions: any;

  // CALENDARIO DISPONIBILIDAD
  selectedCalendarioAlojamiento: AlojamientoResponseDTO | null = null;
  bloqueoFechas: Date[] | null = null; // Changed to allow multiple distinct dates
  disabledDates: Date[] = [];
  mapaDisponibilidad: Map<string, { tipo: string, id?: number }> = new Map();
  sidebarVisible: boolean = false;
  minDate: Date = new Date();

  // Dialog states
  showCreateDialog: boolean = false;
  showEditDialog: boolean = false;
  showSolicitudDialog: boolean = false;
  showServicioDialog: boolean = false;

  // Formulario Alojamiento
  alojamientoForm: AlojamientoRequestDTO = this.getEmptyForm();
  selectedAlojamiento: AlojamientoResponseDTO | null = null;

  // Formulario Servicio
  servicios: ServicioResponseDTO[] = [];
  servicioForm: ServicioRequestDTO = this.getEmptyServicioForm();
  selectedServicio: ServicioResponseDTO | null = null;

  // Variables auxiliares para subir imágenes
  imagenesArchivos: File[] = [];
  imagenesPreviews: string[] = [];
  imagenesAntiguasUrls: string[] = []; // URLs de imágenes ya subidas cuando se edita

  imageUrlInput: string = '';
  imagenesUrls: string[] = []; // URLs nuevas que se agregan manualmente
  isDragOverCreate: boolean = false;
  isDragOverEdit: boolean = false;

  showEditProfileDialog: boolean = false;
  profileForm: UpdateUserDTO = {
    id: 0,
    nombre: '',
    telefono: '',
    fechaNacimiento: '',
    contrasena: '',
    usuarioId: 0
  };

  public readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient, private auth: AuthService, private router: Router, private messageService: MessageService) { }

  ngOnInit() {
    this.auth.currentUser$.subscribe(user => {
      if (user) {
        this.ownerId = user.id || user.usuarioId || 0; // Preferir ID de base de datos
        console.log('[Anfitrion] ownerId (PK) cargado:', this.ownerId);
        this.ownerNombre = user.nombre || 'Nombre del Anfitrión';
        this.ownerEmail = user.email || 'correo@ejemplo.com';

        if (user.imagenPerfil) {
          this.imagenPerfil = this.baseUrl + user.imagenPerfil;
        }

        // Obtener detalle real del backend para imagen actualizada
        this.auth.fetchUsuarioDetalle(this.ownerId).subscribe({
          next: (detalle) => {
            if (detalle.imagenPerfil) {
              this.imagenPerfil = this.baseUrl + detalle.imagenPerfil;
            }
          }
        });

        this.loadAlojamientos();
        this.loadServicios(); // Cargar los servicios por defecto

        // Configuraciones globales para Chart.js
        this.initChartOptions();
      }
    });
  }

  // ============================================
  // 🔹 Subida de Foto de Perfil
  // ============================================
  triggerFileInput() {
    const fileInput = document.querySelector('.profile-avatar input[type="file"]') as HTMLInputElement;
    if (fileInput) {
      fileInput.click();
    }
  }

  onProfileFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.auth.uploadProfileImage(this.ownerId, file).subscribe({
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
  // 🔹 Editar Perfil
  // ============================================
  openEditProfileDialog() {
    this.auth.currentUser$.subscribe(user => {
      if (user) {
        this.profileForm = {
          id: user.id || this.ownerId,
          nombre: user.nombre || this.ownerNombre,
          telefono: '',
          fechaNacimiento: '',
          contrasena: '',
          usuarioId: user.usuarioId || this.ownerId
        };

        // Cargar telefono y fechaNacimiento reales
        this.http.get<any[]>(`${this.API_URL}/usuario`).subscribe(users => {
          const myself = users.find(u => u.usuarioId === this.ownerId);
          if (myself) {
            this.profileForm.nombre = myself.nombre;
            this.profileForm.telefono = myself.telefono || '';
            this.profileForm.fechaNacimiento = myself.fechaNacimiento || '';
          }
          this.showEditProfileDialog = true;
        });
      }
    }).unsubscribe();
  }

  guardarPerfil() {
    this.http.put<any>(
      `${this.API_URL}/usuario/${this.ownerId}`,
      this.profileForm
    ).subscribe({
      next: (data) => {
        this.messageService.add({ severity: 'success', summary: 'Éxito', detail: 'Perfil actualizado con éxito' });
        this.ownerNombre = data.nombre;
        this.showEditProfileDialog = false;
      },
      error: (err) => {
        console.error('Error al editar perfil', err);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error editando perfil' });
      }
    });
  }

  // ============================================
  // 🔹 Navegación y UI
  // ============================================
  changeView(view: string) {
    this.currentView = view as 'perfil' | 'historial' | 'servicios' | 'gestion' | 'calendario' | 'solicitar' | 'metricas';
    this.sidebarVisible = false; // Close sidebar on mobile when navigating
    if (view === 'gestion') this.loadAlojamientos();
    if (view === 'historial') this.loadHistorialReservas();
    if (view === 'servicios') this.loadServicios();
    if (view === 'metricas') this.loadMetrics();
    if (view === 'calendario') this.loadAlojamientos(); // Para el dropdown
  }

  irInicio() {
    this.router.navigate(['/']);
  }

  // ============================================
  // 🔹 Gestión de Servicios
  // ============================================

  getEmptyServicioForm(): ServicioRequestDTO {
    return {
      nombre: '',
      descripcion: '',
      precio: 0
    };
  }

  loadServicios() {
    if (this.loadingServicios) return;
    this.loadingServicios = true;

    this.http.get<ServicioResponseDTO[]>(`${this.API_URL}/servicios`).subscribe({
      next: (data) => {
        this.servicios = data;
        this.loadingServicios = false;
      },
      error: (err) => {
        console.error('Error cargando servicios:', err);
        this.loadingServicios = false;
      }
    });
  }

  openCreateServicioDialog() {
    this.servicioForm = this.getEmptyServicioForm();
    this.selectedServicio = null;
    this.showServicioDialog = true;
  }

  openEditServicioDialog(servicio: ServicioResponseDTO) {
    this.selectedServicio = servicio;
    this.servicioForm = {
      nombre: servicio.nombre,
      descripcion: servicio.descripcion,
      precio: servicio.precio
    };
    this.showServicioDialog = true;
  }

  guardarServicio() {
    if (this.selectedServicio) {
      // Editar
      this.http.put<ServicioResponseDTO>(
        `${this.API_URL}/servicios/${this.selectedServicio.id}`,
        this.servicioForm
      ).subscribe({
        next: () => {
          this.showServicioDialog = false;
          this.loadServicios();
        },
        error: (err) => {
          console.error('Error editando servicio:', err);
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error al editar servicio' });
        }
      });
    } else {
      // Crear
      this.http.post<ServicioResponseDTO>(
        `${this.API_URL}/servicios`,
        this.servicioForm
      ).subscribe({
        next: () => {
          this.showServicioDialog = false;
          this.loadServicios();
        },
        error: (err) => {
          console.error('Error creando servicio:', err);
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error al crear servicio' });
        }
      });
    }
  }

  eliminarServicio(id: number) {
    if (confirm('¿Estás seguro de que quieres eliminar este servicio?')) {
      this.http.delete(`${this.API_URL}/servicios/${id}`).subscribe({
        next: () => {
          this.loadServicios();
        },
        error: (err) => {
          console.error('Error eliminando servicio:', err);
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error al eliminar servicio' });
        }
      });
    }
  }

  // ============================================
  // 🔹 Manejo de Imágenes
  // ============================================
  onFilesSelected(event: any) {
    const files: FileList = event.target.files;
    this.processFiles(files);
  }

  onDragOver(event: DragEvent, type: 'create' | 'edit') {
    event.preventDefault();
    event.stopPropagation();
    if (type === 'create') this.isDragOverCreate = true;
    else this.isDragOverEdit = true;
  }

  onDragLeave(event: DragEvent, type: 'create' | 'edit') {
    event.preventDefault();
    event.stopPropagation();
    if (type === 'create') this.isDragOverCreate = false;
    else this.isDragOverEdit = false;
  }

  onDrop(event: DragEvent, type: 'create' | 'edit') {
    event.preventDefault();
    event.stopPropagation();
    
    if (type === 'create') this.isDragOverCreate = false;
    else this.isDragOverEdit = false;

    if (event.dataTransfer?.files) {
      this.processFiles(event.dataTransfer.files);
    }
  }

  private processFiles(files: FileList) {
    if (files) {
      // Loop over the files using regular for loop to be safe across browsers
      for (let i = 0; i < files.length; i++) {
        const file = files.item(i);
        if (file) {
          this.imagenesArchivos.push(file);

          // Crear preview miniatura inmediata en la UI
          const reader = new FileReader();
          reader.onload = (e: any) => {
            this.imagenesPreviews.push(e.target.result);
          };
          reader.readAsDataURL(file);
        }
      }
    }
  }

  removeImage(index: number, isPreview: boolean) {
    if (isPreview) {
      this.imagenesArchivos.splice(index, 1);
      this.imagenesPreviews.splice(index, 1);
    } else {
      this.imagenesAntiguasUrls.splice(index, 1);
    }
  }

  addImageUrl() {
    if (this.imageUrlInput && this.imageUrlInput.trim() !== '') {
      if (!this.imageUrlInput.startsWith('http://') && !this.imageUrlInput.startsWith('https://')) {
        this.messageService.add({ severity: 'warn', summary: 'Atención', detail: 'Por favor, ingresa una URL válida que empiece con http:// o https://' });
        return;
      }
      this.imagenesUrls.push(this.imageUrlInput.trim());
      this.imageUrlInput = '';
    }
  }

  removeImageUrl(index: number) {
    this.imagenesUrls.splice(index, 1);
  }

  validarMinimoImagenes(): boolean {
    const totalImagenes = this.imagenesArchivos.length + this.imagenesAntiguasUrls.length + this.imagenesUrls.length;
    return totalImagenes >= 3;
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
          this.alojamientos = data.map(a => {
            // Fix relative URIs by prepending localhost server explicitly
            const imgs = a.imagenes?.map(img => {
              return {
                ...img,
                url: img.url.startsWith('http') 
                  ? img.url 
                  : `${environment.apiUrl.replace(/\/$/, '')}/${img.url.replace(/^\/?(api\/)?/, '')}`
              };
            }) || [];
            return {
              ...a,
              estado: a.estado || 'BORRADOR',
              imagenesCompletas: imgs
            };
          });
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
    this.imagenesArchivos = [];
    this.imagenesPreviews = [];
    this.imagenesAntiguasUrls = [];
    this.imagenesUrls = [];
    this.imageUrlInput = '';
    this.showCreateDialog = true;
  }

  crearAlojamiento() {
    if (!this.ownerId || this.ownerId === 0) {
      this.messageService.add({ severity: 'error', summary: 'Error de sesión', detail: 'No se pudo identificar tu cuenta. Por favor, cierra sesión y vuelve a iniciar.' });
      return;
    }

    if (!this.validarMinimoImagenes()) {
      this.messageService.add({ severity: 'warn', summary: 'Imágenes Incompletas', detail: 'Debes agregar al menos 3 imágenes para publicar el alojamiento.' });
      return;
    }

    this.http.post<AlojamientoResponseDTO>(
      `${this.API_URL}/alojamientos?ownerId=${this.ownerId}`,
      this.alojamientoForm
    ).subscribe({
      next: (response) => {
        console.log('Alojamiento creado:', response);
        const newAlojamientoId = response.id;

        // Guardar imágenes físicas y/o URLs
        if (this.imagenesArchivos.length > 0 || this.imagenesUrls.length > 0) {
          const formData = new FormData();
          formData.append('alojamientoId', newAlojamientoId.toString());
          this.imagenesArchivos.forEach(file => {
            formData.append('imagenes', file);
          });
          this.imagenesUrls.forEach(url => {
            formData.append('nuevasUrls', url);
          });

          this.http.post(`${this.API_URL}/imagenes-alojamiento/multiples`, formData).subscribe({
            next: () => {
              console.log('Todas las imágenes subidas con éxito');
              this.finalizarGuardado();
            },
            error: (err) => {
              console.error('Error guardando imágenes', err);
              // Como falló, se podría considerar eliminar el alojamiento creado o advertir al usuario
              this.finalizarGuardado();
            }
          });
        } else {
          this.finalizarGuardado();
        }
      },
      error: (error) => {
        console.error('Error creando alojamiento:', error);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error al crear el alojamiento: ' + (error.error?.message || error.message) });
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
      capacidadMaxima: alojamiento.capacidadMaxima || 1,
      serviciosIds: alojamiento.servicios ? alojamiento.servicios.map(s => s.id) : []
    };
    this.imagenesAntiguasUrls = alojamiento.imagenes ? alojamiento.imagenes.map(i => i.url) : [];
    this.imagenesArchivos = [];
    this.imagenesPreviews = [];
    this.imagenesUrls = [];
    this.imageUrlInput = '';
    this.showEditDialog = true;
  }

  editarAlojamiento() {
    if (!this.selectedAlojamiento) return;

    if (!this.validarMinimoImagenes()) {
      this.messageService.add({ severity: 'warn', summary: 'Imágenes Incompletas', detail: 'Debes agregar al menos 3 imágenes para publicar el alojamiento.' });
      return;
    }

    this.http.put<AlojamientoResponseDTO>(
      `${this.API_URL}/alojamientos/${this.selectedAlojamiento.id}?ownerId=${this.ownerId}`,
      this.alojamientoForm
    ).subscribe({
      next: (response) => {
        console.log('Alojamiento editado:', response);
        const alojamientoId = response.id;

        // Si el usuario eliminó URLs antiguas, las sacamos del backend
        let currImages = this.selectedAlojamiento?.imagenes || [];
        const imgsToDelete = currImages.filter(img => !this.imagenesAntiguasUrls.includes(img.url));

        const deleteRequests = imgsToDelete.map(img =>
          this.http.delete(`${this.API_URL}/imagenes-alojamiento/${img.id}`)
        );

        if (deleteRequests.length > 0) {
          forkJoin(deleteRequests).subscribe({
            next: () => this.guardarNuevasImagenes(alojamientoId),
            error: () => this.guardarNuevasImagenes(alojamientoId) // Intentar guardar de todos modos
          });
        } else {
          this.guardarNuevasImagenes(alojamientoId);
        }
      },
      error: (error) => {
        console.error('Error editando alojamiento:', error);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error editando alojamiento: ' + (error.error?.message || error.message) });
        this.showEditDialog = false;
      }
    });
  }

  private guardarNuevasImagenes(alojamientoId: number) {
    if (this.imagenesArchivos.length > 0 || this.imagenesUrls.length > 0) {
      const formData = new FormData();
      formData.append('alojamientoId', alojamientoId.toString());
      this.imagenesArchivos.forEach(file => {
        formData.append('imagenes', file);
      });
      this.imagenesUrls.forEach(url => {
        formData.append('nuevasUrls', url);
      });

      this.http.post(`${this.API_URL}/imagenes-alojamiento/multiples`, formData).subscribe({
        next: () => this.finalizarGuardado(),
        error: () => this.finalizarGuardado()
      });
    } else {
      this.finalizarGuardado();
    }
  }

  private finalizarGuardado() {
    this.showCreateDialog = false;
    this.showEditDialog = false;
    this.selectedAlojamiento = null;
    this.alojamientoForm = this.getEmptyForm();
    this.imagenesArchivos = [];
    this.imagenesPreviews = [];
    this.imagenesAntiguasUrls = [];
    this.imagenesUrls = [];
    this.imageUrlInput = '';
    this.loadAlojamientos(); // Recargar la lista para traer las imágenes nested reales del backend
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
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error eliminando alojamiento: ' + (error.error?.message || error.message) });
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

  solicitarPublicacion(comentario?: string) {
    if (!this.selectedAlojamiento) return;

    const solicitud: SolicitudPublicacionRequestDTO = {
      usuarioId: this.ownerId,
      alojamientoId: this.selectedAlojamiento.id,
      comentario: comentario
    };

    this.http.post<SolicitudPublicacionResponseDTO>(
      `${this.API_URL}/solicitudes-publicacion`,
      solicitud
    ).subscribe({
      next: (response) => {
        console.log('Solicitud creada:', response);
        this.messageService.add({ severity: 'success', summary: 'Éxito', detail: 'Solicitud de publicación enviada correctamente' });
        this.selectedAlojamiento!.estado = 'PENDIENTE_REVISION';
        this.showSolicitudDialog = false;
        this.selectedAlojamiento = null;
      },
      error: (error) => {
        console.error('Error creando solicitud:', error);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Hubo un error al crear la solicitud: ' + (error.error?.message || error.message) });
        this.showSolicitudDialog = false;
      }
    });
  }

  // ============================================
  // 🔹 Cambiar Estado Visibilidad (Activar/Desactivar)
  // ============================================
  cambiarEstado(alojamiento: AlojamientoResponseDTO, nuevoEstado: string) {
    if (!confirm(`¿Estás seguro de poner el alojamiento en estado ${nuevoEstado}?`)) return;

    this.http.put<AlojamientoResponseDTO>(
      `${this.API_URL}/alojamientos/${alojamiento.id}/estado?estado=${nuevoEstado}&ownerId=${this.ownerId}`,
      null
    ).subscribe({
      next: (response) => {
        this.messageService.add({ severity: 'success', summary: 'Éxito', detail: 'Estado modificado exitosamente' });
        alojamiento.estado = response.estado;
      },
      error: (error) => {
        console.error('Error cambiando estado:', error);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error al cambiar el estado: ' + (error.error?.message || error.message) });
      }
    });
  }

  // ============================================
  // 🔹 Historial de Reservas
  // ============================================
  loadHistorialReservas(filtros?: any) {
    if (this.loadingHistorial) return;

    this.loadingHistorial = true;

    let url = `${this.API_URL}/historial/anfitrion/${this.ownerId}`;
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
          this.historialReservas = [];
        }
      });
  }

  // ============================================
  // 🔹 Confirmar Reserva (Anfitrión)
  // ============================================
  confirmarReservaAnfitrion(reservaId: number) {
    if (!confirm('¿Confirmar esta reserva?')) return;

    this.http.patch(`${this.API_URL}/reservas/${reservaId}/confirmar`, {}).subscribe({
      next: () => {
        this.messageService.add({ severity: 'success', summary: 'Confirmada', detail: `Reserva #${reservaId} confirmada exitosamente.` });
        this.loadHistorialReservas();
      },
      error: (err) => {
        console.error('Error confirmando reserva:', err);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: err.error?.message || 'No se pudo confirmar la reserva.' });
      }
    });
  }

  // ============================================
  // 🔹 Cancelar Reserva (Anfitrión)
  // ============================================
  cancelarReservaAnfitrion(reservaId: number) {
    if (!confirm('¿Cancelar esta reserva? Esta acción no se puede deshacer.')) return;

    const payload = { reservaId: reservaId, motivo: 'Cancelado por el anfitrión' };
    this.http.patch(`${this.API_URL}/reservas/${reservaId}/cancelar`, payload).subscribe({
      next: () => {
        this.messageService.add({ severity: 'info', summary: 'Cancelada', detail: `Reserva #${reservaId} cancelada.` });
        this.loadHistorialReservas();
      },
      error: (err) => {
        console.error('Error cancelando reserva:', err);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: err.error?.message || 'No se pudo cancelar la reserva.' });
      }
    });
  }

  // ============================================
  // 🔹 Helper: clase CSS del estado de reserva
  // ============================================
  getReservaEstadoClass(estado: string): string {
    switch ((estado || '').toUpperCase()) {
      case 'CONFIRMADA':  return 'reserva-estado-confirmada';
      case 'PENDIENTE':   return 'reserva-estado-pendiente';
      case 'CANCELADA':   return 'reserva-estado-cancelada';
      case 'COMPLETADA':  return 'reserva-estado-completada';
      default: return '';
    }
  }

  // ============================================
  // 🔹 Helpers
  // ============================================
  formatDate(dateToken: string): string {
    if (!dateToken) return '';
    const date = new Date(dateToken);
    return date.toLocaleDateString('es-ES', { day: 'numeric', month: 'short', year: 'numeric' });
  }

  getEmptyForm(): AlojamientoRequestDTO {
    return {
      nombre: '',
      descripcion: '',
      direccion: '',
      precio: 0,
      capacidadMaxima: 1,
      serviciosIds: []
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

  // ============================================
  // 🔹 SIDEBAR TOGGLE
  // ============================================
  toggleSidebar() {
    this.sidebarVisible = !this.sidebarVisible;
  }

  // ============================================
  // 🔹 Calendario de Disponibilidad
  // ============================================
  onCalendarioAlojamientoChange() {
    this.disabledDates = [];
    this.bloqueoFechas = null;
    this.mapaDisponibilidad.clear();
    if (!this.selectedCalendarioAlojamiento) return;

    this.loadingMetrics = true; 
    this.http.get<any[]>(`${this.API_URL}/alojamientos/${this.selectedCalendarioAlojamiento.id}/fechas-ocupadas`).subscribe({
      next: (ocupacion) => {
        const dates: Date[] = [];
        ocupacion.forEach((rango: any) => {
          let current = new Date(rango.inicio);
          const end = new Date(rango.fin);
          // Estandarizar a medianoche local
          current = new Date(current.getUTCFullYear(), current.getUTCMonth(), current.getUTCDate());
          const endNorm = new Date(end.getUTCFullYear(), end.getUTCMonth(), end.getUTCDate());

          while (current <= endNorm) {
            const dateCopy = new Date(current);
            const key = dateCopy.toISOString().split('T')[0];
            
            // Si hay solapamiento, preferimos RESERVA sobre BLOQUEO para visualización
            if (!this.mapaDisponibilidad.has(key) || rango.tipo.startsWith('RESERVA')) {
                this.mapaDisponibilidad.set(key, { 
                    tipo: rango.tipo, 
                    id: rango.id 
                });
            }

            // SOLO deshabilitamos si es una RESERVA real (no se puede desbloquear manualmente)
            // Los bloqueos manuales se dejan habitilitados para poder seleccionarlos y borrarlos.
            if (rango.tipo.startsWith('RESERVA')) {
                dates.push(dateCopy);
            }
            
            current.setDate(current.getDate() + 1);
          }
        });
        this.disabledDates = dates;
        this.loadingMetrics = false;
        console.log('[Disponibilidad] Mapa cargado:', this.mapaDisponibilidad);
      },
      error: (err) => {
        console.error('Error cargando disponibilidad:', err);
        this.loadingMetrics = false;
      }
    });
  }

  getDateClass(date: any): string {
    const d = new Date(date.year, date.month, date.day);
    const key = d.toISOString().split('T')[0];
    const info = this.mapaDisponibilidad.get(key);
    
    if (!info) return 'day-available';
    
    if (info.tipo === 'RESERVA_CONFIRMADA') return 'day-reserved-active';
    if (info.tipo === 'RESERVA_PENDIENTE') return 'day-reserved-pending';
    if (info.tipo === 'BLOQUEO_MANUAL') return 'day-manual-block';
    
    return 'day-available';
  }

  getDateTitle(date: any): string {
    const d = new Date(date.year, date.month, date.day);
    const key = d.toISOString().split('T')[0];
    const info = this.mapaDisponibilidad.get(key);
    
    if (!info) return 'Disponible';
    if (info.tipo === 'RESERVA_CONFIRMADA') return 'Reserva Confirmada (Pagada)';
    if (info.tipo === 'RESERVA_PENDIENTE') return 'Reserva Pendiente de Pago';
    if (info.tipo === 'BLOQUEO_MANUAL') return 'Bloqueo Manual (Anfitrión)';
    return 'Ocupado';
  }

  hasBloqueoManualSeleccionado(): boolean {
    if (!this.bloqueoFechas || this.bloqueoFechas.length === 0) return false;
    return this.bloqueoFechas.some(d => {
        const key = d.toISOString().split('T')[0];
        return this.mapaDisponibilidad.get(key)?.tipo === 'BLOQUEO_MANUAL';
    });
  }

  desbloquearFechasSeleccionadas(): void {
    if (!this.bloqueoFechas || this.bloqueoFechas.length === 0) return;

    const idsABorrar = new Set<number>();
    this.bloqueoFechas.forEach(d => {
        const key = d.toISOString().split('T')[0];
        const info = this.mapaDisponibilidad.get(key);
        if (info?.tipo === 'BLOQUEO_MANUAL' && info.id) {
            idsABorrar.add(info.id);
        }
    });

    if (idsABorrar.size === 0) {
        this.messageService.add({ severity: 'info', summary: 'Info', detail: 'No hay bloqueos manuales en la selección.' });
        return;
    }

    if (!confirm(`¿Estás seguro de liberar estas fechas? Se eliminarán ${idsABorrar.size} registro(s) de bloqueo.`)) return;

    const requests = Array.from(idsABorrar).map(id => 
        this.http.delete(`${this.API_URL}/alojamientos/bloqueos/${id}?ownerId=${this.ownerId}`)
    );

    forkJoin(requests).subscribe({
        next: () => {
            this.messageService.add({ severity: 'success', summary: 'Éxito', detail: 'Fechas liberadas correctamente.' });
            this.bloqueoFechas = null;
            this.onCalendarioAlojamientoChange();
        },
        error: (err) => {
            console.error('Error al desbloquear:', err);
            this.messageService.add({ severity: 'error', summary: 'Error', detail: 'No se pudieron liberar todas las fechas.' });
        }
    });
  }

  getToday(): Date {
    const today = new Date();
    today.setHours(0, 0, 0, 0); // Normalizar a medianoche local
    return today;
  }

  bloquearFechasSeleccionadas(): void {
    if (!this.selectedCalendarioAlojamiento || !this.selectedCalendarioAlojamiento.id) {
      this.messageService.add({ severity: 'warn', summary: 'Atención', detail: 'Selecciona un alojamiento primero.' });
      return;
    }

    if (!this.bloqueoFechas || this.bloqueoFechas.length === 0) {
      this.messageService.add({ severity: 'warn', summary: 'Atención', detail: 'Selecciona al menos una fecha en el calendario.' });
      return;
    }

    // Since selectionMode is multiple, bloqueoFechas is an array of distinct dates.
    // We will find min and max to create a conceptual range, or if the backend supports individual blocks,
    // we would send them. Currently, the DTO expects a single startDate and endDate.
    // To support completely sparse selections while the backend accepts one block at a time,
    // we should ideally loop through selected dates treating each as a 1-day block, or restructure.
    // For now, if they select sparse days, we will loop and send a POST for each distinct cluster,
    // or just assume a single range if they select 2 dates. 

    // As requested: "la cantidad de dias que quiera". We will sort them and group them into contiguous ranges,
    // or just send each day as a 1-day block. Doing 1-day blocks is safest for sparse selections:

    const sortedDates = [...this.bloqueoFechas].sort((a, b) => a.getTime() - b.getTime());

    // Group contiguous dates into ranges to minimize HTTP requests
    const ranges: { startDate: Date, endDate: Date }[] = [];
    let currentRange: { startDate: Date, endDate: Date } | null = null;

    for (const date of sortedDates) {
      if (!currentRange) {
        currentRange = { startDate: date, endDate: date };
      } else {
        const nextDay = new Date(currentRange.endDate);
        nextDay.setDate(nextDay.getDate() + 1);

        if (date.getTime() === nextDay.getTime()) {
          // Contiguous, extend range
          currentRange.endDate = date;
        } else {
          // Gap found, push current and start new
          ranges.push(currentRange);
          currentRange = { startDate: date, endDate: date };
        }
      }
    }
    if (currentRange) {
      ranges.push(currentRange);
    }

    const requests = ranges.map(range => {
      const payload = {
        alojamientoId: this.selectedCalendarioAlojamiento!.id,
        fechaInicio: range.startDate.toISOString().split('T')[0],
        fechaFin: range.endDate.toISOString().split('T')[0],
        motivo: 'Bloqueo manual por el Anfitrión'
      };
      return this.http.post(`${this.API_URL}/alojamientos/${this.selectedCalendarioAlojamiento!.id}/bloqueos?ownerId=${this.ownerId}`, payload);
    });

    forkJoin(requests).subscribe({
      next: () => {
        this.messageService.add({ severity: 'success', summary: 'Éxito', detail: 'Fechas bloqueadas correctamente.' });
        this.bloqueoFechas = null;
        this.onCalendarioAlojamientoChange(); // Refresh visual disabled dates
      },
      error: (err) => {
        console.error('Error al bloquear fechas', err);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'No se pudieron bloquear las fechas. Verifica que no estén ya ocupadas.' });
      }
    });
  }

  // ============================================
  // 🔹 Métricas y KPIs (Chart.js)
  // ============================================
  initChartOptions() {
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color');
    const textColorSecondary = documentStyle.getPropertyValue('--text-color-secondary');
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border');

    this.chartOptions = {
      maintainAspectRatio: false,
      aspectRatio: 0.8,
      plugins: {
        legend: {
          labels: {
            color: '#1F2A44'
          }
        }
      },
      scales: {
        x: {
          ticks: {
            color: '#1F2A44',
            font: {
              weight: 500
            }
          },
          grid: {
            color: 'rgba(31, 42, 68, 0.1)',
            drawBorder: false
          }
        },
        y: {
          ticks: {
            color: '#1F2A44'
          },
          grid: {
            color: 'rgba(31, 42, 68, 0.1)',
            drawBorder: false
          }
        }
      }
    };
  }

  loadMetrics() {
    if (this.loadingMetrics) return;
    this.loadingMetrics = true;

    this.http.get<any>(`${this.API_URL}/metrics/owner/${this.ownerId}/dashboard`).subscribe({
      next: (data) => {
        this.totalAlojamientosMetric = data.totalAlojamientos;
        this.totalReservasMetric = data.totalReservas;

        // Configurar gráfico de Dona (Proporción)
        this.pieChartData = {
          labels: ['Alojamientos Publicados', 'Reservas Históricas'],
          datasets: [
            {
              data: [data.totalAlojamientos, data.totalReservas],
              backgroundColor: ['#1F2A44', '#5B2C83'],
              hoverBackgroundColor: ['#151d30', '#4A236B']
            }
          ]
        };

        // Configurar gráfico de barras (Ingresos por mes)
        const labelsMeses = Object.keys(data.ingresosPorMes);
        const dataIngresos = Object.values(data.ingresosPorMes);

        this.barChartData = {
          labels: labelsMeses.length > 0 ? labelsMeses : ['Sin datos'],
          datasets: [
            {
              label: 'Ingresos ($)',
              backgroundColor: '#5B2C83',
              borderColor: '#4A236B',
              data: dataIngresos.length > 0 ? dataIngresos : [0]
            }
          ]
        };

        this.loadingMetrics = false;
      },
      error: (err) => {
        console.error('Error cargando métricas', err);
        this.loadingMetrics = false;
      }
    });
  }

}
