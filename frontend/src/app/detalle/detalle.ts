import { Component, signal, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CarouselModule } from 'primeng/carousel';
import { ButtonModule } from 'primeng/button';
import { Header } from "../components/header/header";
import { AlojamientosService } from '../services/alojamientos';
import { MatDialog, MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { provideNativeDateAdapter, MAT_DATE_FORMATS, MAT_NATIVE_DATE_FORMATS } from '@angular/material/core';
import { AuthService } from '../services/auth.service';

interface AccommodationImage {
  url: string;
  alt: string;
}

interface ReservaRequestDTO {
  alojamientoId: number;
  fechaInicio: string;
  fechaFin: string;
  numeroHuespedes: number;
}

interface ReservaResponseDTO {
  id: number;
  alojamientoNombre: string;
  fechaInicio: string;
  fechaFin: string;
  precioTotal: number;
  estado: string;
}

interface PagoRequestDTO {
  reservaId: number;
  monto: number;
  metodoPago: string;
}

interface ComentarioResponseDTO {
  id?: number;
  nombreUsuario: string;
  mensaje: string;
  calificacion: number;
  respuestaAnfitrion?: string;
  nombreAnfitrion?: string;
  fechaCreacion: string;
}

interface ComentarioRequestDTO {
  alojamientoId: number;
  usuarioId: number;
  contenido: string;
  calificacion: number;
}

interface ComentarioRespuestaRequestDTO {
  comentarioId: number;
  ownerId: number;
  respuesta: string;
}

@Component({
  selector: 'app-detalle',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    CarouselModule,
    ButtonModule,
    MatButtonModule,
    Header
  ],
  providers: [provideNativeDateAdapter()],
  templateUrl: './detalle.html',
  styleUrl: './detalle.scss',
})
export class Detalle implements OnInit {
  private readonly API_URL = 'http://localhost:8080/api';

  alojamientoId = signal<string>('');
  alojamiento: any;
  dialog = inject(MatDialog);

  // Usuario actual
  usuarioId: number | null = null;
  userRole: string | null = null;
  isAuthenticated = false;

  // Comentarios
  comentarios: ComentarioResponseDTO[] = [];
  promedioCalificacion: number = 0;
  nuevoComentarioTexto: string = '';
  nuevaCalificacion: number = 5;

  // Respuestas del Owner
  comentarioRespuestaActual: number | null = null;
  textoRespuesta: string = '';

  accommodation: any = {
    id: 1,
    title: 'Habitación amoblada laureles 6',
    location: 'Medellín, Colombia',
    bedrooms: 2,
    bathrooms: 'Baño compartido',
    guests: 4,
    price: 150000,
    rating: 4.8,
    reviews: 127,
    description: 'Hermosa habitación amoblada en el corazón de Laureles.',
    amenities: [
      'WiFi de alta velocidad',
      'Cocina equipada',
      'Aire acondicionado',
      'TV con cable',
      'Lavadora',
      'Zona de trabajo',
      'Estacionamiento gratuito',
      'Seguridad 24/7'
    ],
    images: [
      {
        url: 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800',
        alt: 'Vista principal'
      },
      {
        url: 'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800',
        alt: 'Sala de estar'
      },
      {
        url: 'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800',
        alt: 'Habitación'
      }
    ]
  };

  responsiveOptions = [
    { breakpoint: '1024px', numVisible: 5 },
    { breakpoint: '768px', numVisible: 3 },
    { breakpoint: '560px', numVisible: 1 }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private alojamientosService: AlojamientosService,
    private http: HttpClient,
    private auth: AuthService
  ) { }

  ngOnInit() {
    this.auth.isAuthenticated$.subscribe((isAuth: boolean) => this.isAuthenticated = isAuth);
    this.auth.currentUser$.subscribe((user: any) => {
      this.usuarioId = user?.id || null;
      this.userRole = user?.role || null;
    });

    this.route.paramMap.subscribe(params => {
      this.alojamientoId.set(params.get('id') || '');
      if (this.alojamientoId()) {
        this.getById(this.alojamientoId());
        this.loadComentarios(this.alojamientoId());
      }
    });
  }

  getById(id: string) {
    this.alojamientosService.obtenerAlojamientoPorId(Number(id)).subscribe({
      next: (data) => {
        this.alojamiento = data;
        // Actualizar accommodation con datos reales
        this.accommodation = {
          ...this.accommodation,
          id: data.id,
          title: data.nombre,
          location: data.ubicacion || data.direccion,
          price: data.precioPorNoche || data.precio,
          description: data.descripcion,
          guests: data.capacidad,
          bedrooms: data.numHabitaciones,
          bathrooms: data.numBanos ? `${data.numBanos} baños` : 'Baño compartido',
          amenities: data.servicios || this.accommodation.amenities,
          images: data.imagenes?.map((img: string, index: number) => ({
            url: img,
            alt: `${data.nombre} - Imagen ${index + 1}`
          })) || this.accommodation.images
        };
      },
      error: (error) => {
        console.error('Error al obtener alojamiento:', error);
        alert('Error al cargar el alojamiento. Usando datos de ejemplo.');
      }
    });
  }

  loadComentarios(id: string) {
    this.http.get<ComentarioResponseDTO[]>(`${this.API_URL}/comentarios/alojamiento/${id}`)
      .subscribe({
        next: (data) => {
          this.comentarios = data;
          if (data && data.length > 0) {
            const sum = data.reduce((acc, curr) => acc + curr.calificacion, 0);
            this.promedioCalificacion = Number((sum / data.length).toFixed(1));
            this.accommodation.rating = this.promedioCalificacion;
            this.accommodation.reviews = data.length;
          } else {
            this.promedioCalificacion = 0;
            this.accommodation.rating = 0;
            this.accommodation.reviews = 0;
          }
        },
        error: (error) => {
          console.error('Error cargando comentarios', error);
        }
      });
  }

  enviarComentario() {
    if (!this.isAuthenticated || !this.usuarioId) {
      alert('Debes iniciar sesión para dejar un comentario.');
      return;
    }
    if (!this.nuevoComentarioTexto.trim()) {
      alert('El comentario no puede estar vacío.');
      return;
    }

    const payload: ComentarioRequestDTO = {
      alojamientoId: Number(this.alojamientoId()),
      usuarioId: this.usuarioId,
      contenido: this.nuevoComentarioTexto,
      calificacion: this.nuevaCalificacion
    };

    this.http.post(`${this.API_URL}/comentarios`, payload).subscribe({
      next: () => {
        alert('Comentario publicado exitosamente.');
        this.nuevoComentarioTexto = '';
        this.nuevaCalificacion = 5;
        this.loadComentarios(this.alojamientoId());
      },
      error: (error) => {
        console.error('Error enviando comentario', error);
        if (error.status === 400 || error.error) {
          alert(error.error || 'No tienes una reserva confirmada y pasada para este alojamiento.');
        } else {
          alert('Ocurrió un error al enviar el comentario.');
        }
      }
    });
  }

  isOwner(): boolean {
    return this.userRole === 'OWNER' && this.alojamiento?.ownerId === this.usuarioId;
  }

  abrirRespuesta(comentarioId: number) {
    this.comentarioRespuestaActual = comentarioId;
    this.textoRespuesta = '';
  }

  cancelarRespuesta() {
    this.comentarioRespuestaActual = null;
    this.textoRespuesta = '';
  }

  responderComentario(comentarioId: number) {
    if (!this.textoRespuesta.trim()) {
      alert('La respuesta no puede estar vacía.');
      return;
    }

    const payload: ComentarioRespuestaRequestDTO = {
      comentarioId: comentarioId,
      ownerId: this.usuarioId!,
      respuesta: this.textoRespuesta
    };

    this.http.post(`${this.API_URL}/comentarios/responder`, payload).subscribe({
      next: () => {
        alert('Respuesta publicada exitosamente.');
        this.cancelarRespuesta();
        this.loadComentarios(this.alojamientoId());
      },
      error: (error) => {
        console.error('Error respondiendo comentario', error);
        alert('Error al publicar la respuesta.');
      }
    });
  }

  onReserve() {
    const dialogRef = this.dialog.open(ReservaDialog, {
      width: '600px',
      data: {
        alojamiento: this.alojamiento || this.accommodation,
        precioNoche: this.alojamiento?.precioPorNoche || this.accommodation.price,
        capacidadMaxima: this.alojamiento?.capacidad || this.accommodation.guests
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.crearReserva(result);
      }
    });
  }

  crearReserva(datos: any) {
    const reservaDTO: ReservaRequestDTO = {
      alojamientoId: Number(this.alojamientoId()),
      fechaInicio: this.formatDateToString(datos.fechaInicio),
      fechaFin: this.formatDateToString(datos.fechaFin),
      numeroHuespedes: datos.numeroHuespedes
    };

    console.log('Creando reserva:', reservaDTO);

    this.http.post<ReservaResponseDTO>(
      `${this.API_URL}/reservas`,
      reservaDTO
    ).subscribe({
      next: (reserva) => {
        console.log('Reserva creada:', reserva);
        alert(`¡Reserva creada exitosamente!\n\nTotal: $${reserva.precioTotal.toLocaleString()}\nEstado: ${reserva.estado}`);

        this.procesarPago(reserva);
      },
      error: (error) => {
        console.error('Error creando reserva:', error);
        alert('Error al crear la reserva. Por favor intenta nuevamente.');
      }
    });
  }

  procesarPago(reserva: ReservaResponseDTO) {
    const pagoDTO: PagoRequestDTO = {
      reservaId: reserva.id,
      monto: reserva.precioTotal,
      metodoPago: 'TARJETA'
    };

    this.http.post(`${this.API_URL}/pagos`, pagoDTO).subscribe({
      next: (pago) => {
        console.log('Pago registrado:', pago);
        alert('¡Pago procesado exitosamente! Redirigiendo a tus reservas...');

        setTimeout(() => {
          this.router.navigate(['/dashboard/reservas']);
        }, 2000);
      },
      error: (error) => {
        console.error('Error procesando pago:', error);
        alert('Reserva creada pero hubo un error en el pago.\nPor favor contacta soporte con el ID de reserva: ' + reserva.id);
      }
    });
  }

  formatDateToString(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  onBack() {
    this.router.navigate(['/']);
  }
}

// ============================================
// DIALOG DE RESERVA
// ============================================
@Component({
  selector: 'reserva-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule
  ],
  providers: [
    provideNativeDateAdapter(),
    { provide: MAT_DATE_FORMATS, useValue: MAT_NATIVE_DATE_FORMATS }
  ],
  template: `
    <div class="reserva-dialog">
      <h2 mat-dialog-title>Reservar Alojamiento</h2>
      <p class="subtitle">{{ data.alojamiento?.nombre || 'Alojamiento' }}</p>
      
      <mat-dialog-content>
        <div class="form-group">
          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Fecha de Check-in</mat-label>
            <input 
              matInput 
              [matDatepicker]="pickerInicio" 
              [(ngModel)]="fechaInicio" 
              [min]="minDate"
              (dateChange)="calcularPrecio()"
              required
            >
            <mat-datepicker-toggle matIconSuffix [for]="pickerInicio"></mat-datepicker-toggle>
            <mat-datepicker #pickerInicio></mat-datepicker>
          </mat-form-field>
        </div>

        <div class="form-group">
          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Fecha de Check-out</mat-label>
            <input 
              matInput 
              [matDatepicker]="pickerFin" 
              [(ngModel)]="fechaFin" 
              [min]="getFechaMinFin()"
              (dateChange)="calcularPrecio()"
              required
            >
            <mat-datepicker-toggle matIconSuffix [for]="pickerFin"></mat-datepicker-toggle>
            <mat-datepicker #pickerFin></mat-datepicker>
          </mat-form-field>
        </div>

        <div class="form-group">
          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Número de huéspedes</mat-label>
            <input 
              matInput 
              type="number" 
              [(ngModel)]="numeroHuespedes" 
              [min]="1" 
              [max]="data.capacidadMaxima"
              required
            >
            <mat-hint>Máximo: {{ data.capacidadMaxima }} huéspedes</mat-hint>
          </mat-form-field>
        </div>

        <div class="resumen" *ngIf="fechaInicio && fechaFin">
          <h3>Resumen de la reserva</h3>
          <div class="resumen-item">
            <span>Precio por noche:</span>
            <span>\${{ data.precioNoche | number }}</span>
          </div>
          <div class="resumen-item">
            <span>Noches:</span>
            <span>{{ calcularDias() }}</span>
          </div>
          <div class="resumen-divider"></div>
          <div class="resumen-item total">
            <span><strong>Total a pagar:</strong></span>
            <span><strong>\${{ precioTotal | number }}</strong></span>
          </div>
        </div>

        <div class="info-box" *ngIf="!fechaInicio || !fechaFin">
          <i class="pi pi-info-circle"></i>
          <p>Selecciona las fechas para ver el resumen de tu reserva</p>
        </div>
      </mat-dialog-content>

      <mat-dialog-actions align="end">
        <button mat-button (click)="onCancel()">Cancelar</button>
        <button 
          mat-raised-button 
          color="primary" 
          [disabled]="!isValid()" 
          (click)="onConfirm()"
        >
          Confirmar Reserva
        </button>
      </mat-dialog-actions>
    </div>
  `,
  styles: [`
    .reserva-dialog {
      padding: 8px;
    }

    .subtitle {
      color: #6b7280;
      font-size: 16px;
      margin: -8px 0 24px;
    }

    mat-dialog-content {
      padding: 20px 0;
      min-height: 400px;
    }

    .form-group {
      margin-bottom: 20px;
    }

    .full-width {
      width: 100%;
    }

    .resumen {
      background: linear-gradient(135deg, #f3e8ff 0%, #e9d5ff 100%);
      padding: 20px;
      border-radius: 12px;
      margin-top: 24px;
      border: 2px solid #c4b5fd;

      h3 {
        margin: 0 0 16px;
        color: #5b21b6;
        font-size: 18px;
        font-weight: 700;
      }

      .resumen-item {
        display: flex;
        justify-content: space-between;
        padding: 8px 0;
        color: #1e1b4b;
        font-size: 15px;

        &.total {
          font-size: 20px;
          color: #5b21b6;
          margin-top: 8px;
        }
      }

      .resumen-divider {
        height: 1px;
        background: #c4b5fd;
        margin: 12px 0;
      }
    }

    .info-box {
      background: #dbeafe;
      padding: 16px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      gap: 12px;
      margin-top: 20px;

      i {
        color: #1e40af;
        font-size: 24px;
      }

      p {
        margin: 0;
        color: #1e40af;
        font-size: 14px;
      }
    }

    mat-dialog-actions {
      margin-top: 20px;
      padding: 16px 0 0;
      border-top: 1px solid #e5e7eb;

      button {
        margin-left: 8px;
      }
    }

    ::ng-deep {
      .mat-mdc-raised-button.mat-primary {
        background-color: #7c3aed !important;

        &:hover:not(:disabled) {
          background-color: #6d28d9 !important;
        }
      }

      .mat-mdc-form-field-focus-overlay {
        background-color: transparent;
      }

      .mat-mdc-form-field.mat-focused {
        .mdc-notched-outline__leading,
        .mdc-notched-outline__notch,
        .mdc-notched-outline__trailing {
          border-color: #7c3aed !important;
        }

        .mat-mdc-floating-label {
          color: #7c3aed !important;
        }
      }
    }
  `]
})
export class ReservaDialog {
  dialogRef = inject<MatDialogRef<ReservaDialog>>(MatDialogRef<ReservaDialog>);
  data = inject(MAT_DIALOG_DATA);

  fechaInicio: Date | null = null;
  fechaFin: Date | null = null;
  numeroHuespedes: number = 1;
  precioTotal: number = 0;
  minDate = new Date();

  getFechaMinFin(): Date {
    if (!this.fechaInicio) return this.minDate;
    const minFin = new Date(this.fechaInicio);
    minFin.setDate(minFin.getDate() + 1);
    return minFin;
  }

  calcularDias(): number {
    if (!this.fechaInicio || !this.fechaFin) return 0;
    const diffTime = Math.abs(this.fechaFin.getTime() - this.fechaInicio.getTime());
    const dias = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return dias;
  }

  calcularPrecio() {
    const dias = this.calcularDias();
    this.precioTotal = dias * this.data.precioNoche;
  }

  isValid(): boolean {
    return !!(
      this.fechaInicio &&
      this.fechaFin &&
      this.numeroHuespedes > 0 &&
      this.numeroHuespedes <= this.data.capacidadMaxima
    );
  }

  onCancel() {
    this.dialogRef.close();
  }

  onConfirm() {
    if (this.isValid()) {
      this.dialogRef.close({
        fechaInicio: this.fechaInicio,
        fechaFin: this.fechaFin,
        numeroHuespedes: this.numeroHuespedes,
        precioTotal: this.precioTotal
      });
    }
  }
}