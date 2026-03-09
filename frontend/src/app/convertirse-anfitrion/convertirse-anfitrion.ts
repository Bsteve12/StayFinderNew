import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { FileUploadModule } from 'primeng/fileupload';
import { ToastModule } from 'primeng/toast';
import { DividerModule } from 'primeng/divider';


// Interfaces
interface SolicitudOwnerRequestDTO {
  usuarioId: number;
  comentario: string;
}

interface SolicitudOwnerResponseDTO {
  id: number;
  usuarioId: number;
  nombreUsuario: string;
  emailUsuario: string;
  estado: 'PENDIENTE' | 'APROBADA' | 'RECHAZADA';
  comentario: string;
  fechaSolicitud: string;
  fechaRevision?: string;
  documentoRuta?: string;
  adminRevisorId?: number;
}

interface Usuario {
  id: number;
  nombre: string;
  email: string;
  rol?: string;
}

@Component({
  selector: 'app-convertirse-anfitrion',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    HttpClientModule,
    ButtonModule,
    FileUploadModule,
    ToastModule,
    DividerModule
  ],
  templateUrl: './convertirse-anfitrion.html',
  styleUrl: './convertirse-anfitrion.scss',
  providers: [MessageService]
})

export class ConvertirseAnfitrion implements OnInit {
  private readonly API_URL = 'http://localhost:8080/api';

  // Estado del componente
  solicitudForm: FormGroup;
  documentoSeleccionado: File | null = null;
  documentoPreview: SafeResourceUrl | null = null;
  loading: boolean = false;
  tieneSolicitudPendiente: boolean = false;

  // Usuario actual (debería venir del servicio de autenticación)
  usuarioActual: Usuario = {
    id: 1,
    nombre: 'Usuario Test',
    email: 'usuario@test.com',
    rol: 'CLIENTE'
  };

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private messageService: MessageService,
    private http: HttpClient,
    private sanitizer: DomSanitizer
  ) {
    this.solicitudForm = this.fb.group({
      comentario: ['', [Validators.required, Validators.minLength(50), Validators.maxLength(500)]],
      documento: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    this.verificarUsuarioLogueado();
    this.verificarSolicitudPendiente();
  }

  // ============================================
  // 🔹 Verificaciones
  // ============================================
  verificarUsuarioLogueado(): void {
    const token = localStorage.getItem('token');
    const userStr = localStorage.getItem('user');

    if (!token || !userStr) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Sesión requerida',
        detail: 'Debe iniciar sesión para continuar'
      });
      setTimeout(() => this.router.navigate(['/ingresar']), 2000);
      return;
    }

    try {
      this.usuarioActual = JSON.parse(userStr);
    } catch (error) {
      console.error('Error al parsear usuario:', error);
      this.router.navigate(['/ingresar']);
    }
  }

  verificarSolicitudPendiente(): void {
    this.http.get<boolean>(`${this.API_URL}/solicitudes-owner/verificar-pendiente/${this.usuarioActual.id}`)
      .subscribe({
        next: (tieneSolicitud) => {
          this.tieneSolicitudPendiente = tieneSolicitud;
          if (tieneSolicitud) {
            this.messageService.add({
              severity: 'info',
              summary: 'Solicitud pendiente',
              detail: 'Ya tienes una solicitud en revisión'
            });
          }
        },
        error: (error) => {
          console.error('Error al verificar solicitud:', error);
        }
      });
  }

  // ============================================
  // 🔹 Manejo de Archivos
  // ============================================
  onFileSelect(event: any): void {
    const file = event.files[0];
    if (!file) return;

    // Validar tipo de archivo
    if (file.type !== 'application/pdf') {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Solo se permiten archivos PDF'
      });
      event.clear();
      return;
    }

    // Validar tamaño (máximo 5MB)
    if (file.size > 5 * 1024 * 1024) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'El archivo no debe superar 5MB'
      });
      event.clear();
      return;
    }

    this.documentoSeleccionado = file;
    const objectUrl = URL.createObjectURL(file);
    this.documentoPreview = this.sanitizer.bypassSecurityTrustResourceUrl(objectUrl);

    this.solicitudForm.patchValue({ documento: file });
    this.solicitudForm.get('documento')?.updateValueAndValidity();

    this.messageService.add({
      severity: 'success',
      summary: 'Archivo cargado',
      detail: `${file.name} seleccionado correctamente`
    });
  }

  onFileRemove(): void {
    this.documentoSeleccionado = null;
    this.documentoPreview = null;
    this.solicitudForm.patchValue({ documento: null });
    this.solicitudForm.get('documento')?.updateValueAndValidity();
  }

  // ============================================
  // 🔹 Enviar Solicitud
  // ============================================
  enviarSolicitud(): void {
    // Validar formulario
    if (this.solicitudForm.invalid) {
      this.marcarCamposComoTocados();
      this.messageService.add({
        severity: 'warn',
        summary: 'Formulario incompleto',
        detail: 'Por favor complete todos los campos requeridos'
      });
      return;
    }

    if (!this.usuarioActual) {
      this.router.navigate(['/login']);
      return;
    }

    this.loading = true;

    // Crear Payload
    const requestData = {
      usuarioId: this.usuarioActual.id,
      comentario: this.solicitudForm.value.comentario
    };

    // Crear FormData y adjuntar el blob JSON
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(requestData)], { type: 'application/json' }));

    if (this.documentoSeleccionado) {
      formData.append('documento', this.documentoSeleccionado);
    }

    // Enviar solicitud
    this.http.post<SolicitudOwnerResponseDTO>(
      `${this.API_URL}/solicitudes-owner`,
      formData
    ).subscribe({
      next: (response) => {
        console.log('Solicitud creada:', response);
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Solicitud enviada correctamente. Será revisada por un administrador.'
        });

        setTimeout(() => {
          this.loading = false;
          this.router.navigate(['/inicio']);
        }, 2000);
      },
      error: (error) => {
        console.error('Error al enviar solicitud:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: error.error?.message || 'No se pudo enviar la solicitud. Intente nuevamente.'
        });
        this.loading = false;
      }
    });
  }

  // ============================================
  // 🔹 Navegación
  // ============================================
  volverAlInicio(): void {
    this.router.navigate(['/']);
  }

  irALogin(): void {
    this.router.navigate(['/login']);
  }

  // ============================================
  // 🔹 Helpers
  // ============================================
  marcarCamposComoTocados(): void {
    Object.keys(this.solicitudForm.controls).forEach(key => {
      this.solicitudForm.get(key)?.markAsTouched();
    });
  }

  resetForm(): void {
    this.solicitudForm.reset();
    this.documentoSeleccionado = null;
    this.documentoPreview = null;
  }

  get comentarioInvalido(): boolean {
    const control = this.solicitudForm.get('comentario');
    return !!(control && control.invalid && control.touched);
  }

  get documentoInvalido(): boolean {
    const control = this.solicitudForm.get('documento');
    return !!(control && control.invalid && control.touched);
  }

  get caracteresComentario(): number {
    return this.solicitudForm.get('comentario')?.value?.length || 0;
  }
}
