import { Component, signal, OnInit, inject, ViewEncapsulation } from '@angular/core';
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
import { MessageService } from 'primeng/api';
import { environment } from '../../environments/environment';
import { DatePickerModule } from 'primeng/datepicker';
import { Observable, firstValueFrom } from 'rxjs';

interface AccommodationImage {
  url: string;
  alt: string;
}

interface ReservaRequestDTO {
  usuarioId: number;
  alojamientoId: number;
  fechaInicio: string;
  fechaFin: string;
  numeroHuespedes: number;
  tipoReserva: string;
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
  private readonly API_URL = `${environment.apiUrl}/api`;

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
    { breakpoint: '1024px', numVisible: 5, numScroll: 1 },
    { breakpoint: '768px', numVisible: 3, numScroll: 1 },
    { breakpoint: '560px', numVisible: 1, numScroll: 1 }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private alojamientosService: AlojamientosService,
    private http: HttpClient,
    private auth: AuthService,
    private messageService: MessageService
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
        const d = data as any;
        this.accommodation = {
          ...this.accommodation,
          id: d.id,
          title: d.nombre,
          location: d.ubicacion || d.direccion,
          price: d.precioPorNoche || d.precio,
          description: d.descripcion,
          guests: d.capacidad || d.capacidadMaxima,
          bedrooms: d.numHabitaciones || 1, // Fallback if property doesn't exist
          bathrooms: d.numBanos ? `${d.numBanos} baños` : 'Baño compartido',
          amenities: d.servicios || this.accommodation.amenities,
          images: d.imagenes?.map((img: any, index: number) => {
            const rawUrl = img.url || img;
            return {
              url: rawUrl.startsWith('/api') ? `${environment.apiUrl}${rawUrl}` : rawUrl,
              alt: `${d.nombre} - Imagen ${index + 1}`
            };
          }) || this.accommodation.images
        };
      },
      error: (error) => {
        console.error('Error al obtener alojamiento:', error);
        this.messageService.add({ severity: 'warn', summary: 'Aviso', detail: 'Error al cargar el alojamiento. Usando datos de ejemplo.' });
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
      this.messageService.add({ severity: 'warn', summary: 'Atención', detail: 'Debes iniciar sesión para dejar un comentario.' });
      return;
    }
    if (!this.nuevoComentarioTexto.trim()) {
      this.messageService.add({ severity: 'warn', summary: 'Atención', detail: 'El comentario no puede estar vacío.' });
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
        this.messageService.add({ severity: 'success', summary: 'Éxito', detail: 'Comentario publicado exitosamente.' });
        this.nuevoComentarioTexto = '';
        this.nuevaCalificacion = 5;
        this.loadComentarios(this.alojamientoId());
      },
      error: (error) => {
        console.error('Error enviando comentario', error);
        if (error.status === 400 || error.error) {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: error.error || 'No tienes una reserva confirmada y pasada para este alojamiento.' });
        } else {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Ocurrió un error al enviar el comentario.' });
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
      this.messageService.add({ severity: 'warn', summary: 'Atención', detail: 'La respuesta no puede estar vacía.' });
      return;
    }

    const payload: ComentarioRespuestaRequestDTO = {
      comentarioId: comentarioId,
      ownerId: this.usuarioId!,
      respuesta: this.textoRespuesta
    };

    this.http.post(`${this.API_URL}/comentarios/responder`, payload).subscribe({
      next: () => {
        this.messageService.add({ severity: 'success', summary: 'Éxito', detail: 'Respuesta publicada exitosamente.' });
        this.cancelarRespuesta();
        this.loadComentarios(this.alojamientoId());
      },
      error: (error) => {
        console.error('Error respondiendo comentario', error);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error al publicar la respuesta.' });
      }
    });
  }

  procesandoReserva: boolean = false;

  onReserve() {
    if (!this.isAuthenticated || !this.usuarioId) {
      this.messageService.add({ severity: 'warn', summary: 'Atención', detail: 'Debes iniciar sesión para realizar una reserva.' });
      return;
    }
    if (this.procesandoReserva) return;
    this.procesandoReserva = true;

    const dialogRef = this.dialog.open(ReservaDialog, {
      width: '600px',
      data: {
        alojamiento: this.alojamiento || this.accommodation,
        precioNoche: this.alojamiento?.precioPorNoche || this.accommodation.price,
        capacidadMaxima: this.alojamiento?.capacidad || this.accommodation.guests
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      this.procesandoReserva = false;
      if (result) {
        this.crearReserva(result);
      }
    });
  }

  crearReserva(datos: any) {
    if (this.procesandoReserva) return;
    this.procesandoReserva = true;

    const reservaDTO: ReservaRequestDTO = {
      usuarioId: this.usuarioId!,
      alojamientoId: Number(this.alojamientoId()),
      fechaInicio: this.formatDateToString(datos.fechaInicio),
      fechaFin: this.formatDateToString(datos.fechaFin),
      numeroHuespedes: datos.numeroHuespedes,
      tipoReserva: 'SENCILLA'
    };

    console.log('Creando reserva:', reservaDTO);

    this.http.post<ReservaResponseDTO>(
      `${this.API_URL}/reservas`,
      reservaDTO
    ).subscribe({
      next: (reserva) => {
        console.log('Reserva creada:', reserva);
        this.messageService.add({
          severity: 'success',
          summary: '¡Reserva creada!',
          detail: `Tu reserva está PENDIENTE. Completa el pago para confirmarla.`
        });

        // Redirigir a la página de pago con los datos de la reserva
        setTimeout(() => {
          this.router.navigate(['/pagar-reserva'], {
            queryParams: {
              reservaId: reserva.id,
              monto: reserva.precioTotal,
              alojamiento: reserva.alojamientoNombre || this.accommodation?.title || 'Alojamiento Reservado',
              fechaInicio: reserva.fechaInicio,
              fechaFin: reserva.fechaFin
            }
          });
        }, 1500);
      },
      error: (error) => {
        this.procesandoReserva = false;
        console.error('Error creando reserva:', error);
        
        let errorMsg = 'Error al crear la reserva. Por favor intenta nuevamente.';
        if (error.error) {
          if (typeof error.error === 'string') {
            errorMsg = error.error;
          } else if (error.error.message) {
            errorMsg = error.error.message;
          } else if (error.error.error) {
            errorMsg = error.error.error;
          }
        }
        
        this.messageService.add({ severity: 'error', summary: 'Aviso del Servidor', detail: errorMsg });
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
// DIALOG DE RESERVA (VERSIÓN ROBUSTA 100%)
// ============================================
@Component({
  selector: 'reserva-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    DatePickerModule
  ],
  template: `
    <div class="reserva-redo">
      <h2 mat-dialog-title>Crea tu Reserva</h2>
      
      <mat-dialog-content>
        <div class="sf-form-section">
          <label class="sf-label">Selecciona el rango de fechas</label>
          <p-datepicker 
            [(ngModel)]="rangoFechas" 
            selectionMode="range" 
            [minDate]="minDate" 
            [readonlyInput]="true" 
            [showIcon]="true"
            [disabledDates]="fechasOcupadas"
            placeholder="Clic para elegir entrada y salida"
            (onSelect)="onDateSelect()"
            appendTo="body"
            styleClass="sf-calendar-range"
            [inputStyle]="{'width': '100%', 'padding': '12px', 'border-radius': '8px'}"
          >
            <ng-template pTemplate="date" let-date>
                <span class="sf-calendar-cell" [ngClass]="getDateClass(date)" [title]="getDateTitle(date)">
                    {{date.day}}
                </span>
            </ng-template>
          </p-datepicker>
          <small class="sf-help">Haz clic primero en la entrada y luego en la salida.</small>
        </div>

        <div class="sf-form-section mt-4">
          <mat-form-field appearance="outline" class="w-full">
            <mat-label>Número de Huéspedes</mat-label>
            <input matInput type="number" [(ngModel)]="numeroHuespedes" [min]="1" [max]="data.capacidadMaxima" (change)="onDateSelect()">
            <mat-hint>Capacidad máxima: {{ data.capacidadMaxima }}</mat-hint>
          </mat-form-field>
        </div>

        <div class="sf-summary-card" *ngIf="fechaInicio && fechaFin">
          <div class="sf-summary-row">
            <span>Estancia ({{ calcularDias() }} {{ calcularDias() === 1 ? 'noche' : 'noches' }})</span>
            <span>{{ precioTotal | currency:'USD':'symbol':'1.0-0' }}</span>
          </div>
          <div class="sf-summary-divider"></div>
          <div class="sf-summary-total">
            <span>Total estimado</span>
            <span>{{ precioTotal | currency:'USD':'symbol':'1.0-0' }}</span>
          </div>
        </div>

        <div class="sf-info-alert" *ngIf="!fechaInicio || !fechaFin">
          <p>Selecciona ambas fechas para calcular el valor total.</p>
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
          Confirmar y Pagar
        </button>
      </mat-dialog-actions>
    </div>
  `,
  styles: [`
    .reserva-redo {
      padding: 10px;
      min-width: 320px;
    }
    .sf-form-section {
      margin-bottom: 24px;
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
    .sf-label {
      font-weight: 600;
      color: #374151;
      font-size: 14px;
    }
    .sf-help {
      color: #6b7280;
      font-size: 12px;
    }
    .sf-calendar-range {
      width: 100%;
    }
    .sf-summary-card {
      background: #f8fafc;
      border: 1px solid #e2e8f0;
      border-radius: 12px;
      padding: 20px;
      margin-top: 10px;
    }
    .sf-summary-row {
      display: flex;
      justify-content: space-between;
      color: #475569;
      margin-bottom: 12px;
    }
    .sf-summary-divider {
      height: 1px;
      background: #e2e8f0;
      margin: 12px 0;
    }
    .sf-summary-total {
      display: flex;
      justify-content: space-between;
      font-weight: 700;
      font-size: 18px;
      color: #1e293b;
    }
    .sf-info-alert {
      padding: 16px;
      background: #eff6ff;
      border-radius: 8px;
    }

    .mat-mdc-form-field-focus-overlay {
      background-color: transparent;
    }

    .mat-mdc-form-field.mat-focused .mdc-notched-outline__leading,
    .mat-mdc-form-field.mat-focused .mdc-notched-outline__notch,
    .mat-mdc-form-field.mat-focused .mdc-notched-outline__trailing {
      border-color: #7c3aed !important;
    }

    .mat-mdc-form-field.mat-focused .mat-mdc-floating-label {
      color: #7c3aed !important;
    }
  `]
})
export class ReservaDialog implements OnInit {
  dialogRef = inject<MatDialogRef<ReservaDialog>>(MatDialogRef<ReservaDialog>);
  data = inject(MAT_DIALOG_DATA);
  private alojamientosService = inject(AlojamientosService);

  rangoFechas: Date[] | null = null;
  fechaInicio: Date | null = null;
  fechaFin: Date | null = null;
  fechasOcupadas: Date[] = [];
  mapaDisponibilidad: Map<string, string> = new Map();
  numeroHuespedes: number = 1;
  precioTotal: number = 0;
  minDate = new Date();

  ngOnInit() {
    this.cargarFechasOcupadas();
  }

  async cargarFechasOcupadas() {
    // 🔹 Robust ID detection: checking both lowercase and uppercase 'id' properties if they exist
    const id = this.data.alojamiento?.id || this.data.alojamiento?.alojamientoId;
    if (!id) {
      console.warn('[ReservaDialog] No se encontró ID de alojamiento para cargar disponibilidad');
      return;
    }
    
    try {
      const ocupacion = await firstValueFrom(this.alojamientosService.getFechasOcupadas(Number(id)));
      const datesToDisable: Date[] = [];
      
      ocupacion.forEach((rango: any) => {
        const start = new Date(rango.inicio);
        const end = new Date(rango.fin);
        
        // NORMALIZACIÓN A MEDIANOCHE LOCAL (Evita problemas de Zona Horaria UTC)
        let current = new Date(start.getUTCFullYear(), start.getUTCMonth(), start.getUTCDate());
        const endNorm = new Date(end.getUTCFullYear(), end.getUTCMonth(), end.getUTCDate());
        
        while (current <= endNorm) {
          const dateCopy = new Date(current);
          const key = dateCopy.toISOString().split('T')[0];
          
          this.mapaDisponibilidad.set(key, rango.tipo);
          datesToDisable.push(dateCopy);
          
          current.setDate(current.getDate() + 1);
        }
      });
      
      this.fechasOcupadas = datesToDisable;
      console.log(`[ReservaDialog] ${this.fechasOcupadas.length} fechas ocupadas cargadas. Mapa:`, this.mapaDisponibilidad);
    } catch (error) {
      console.error('Error al cargar disponibilidad:', error);
    }
  }

  getDateClass(date: any): string {
    const d = new Date(date.year, date.month, date.day);
    const key = d.toISOString().split('T')[0];
    const tipo = this.mapaDisponibilidad.get(key);
    
    if (!tipo) return 'day-available';
    if (tipo.startsWith('RESERVA')) return 'day-reserved-active';
    if (tipo === 'BLOQUEO_MANUAL') return 'day-manual-block';
    return 'day-available';
  }

  getDateTitle(date: any): string {
    const d = new Date(date.year, date.month, date.day);
    const key = d.toISOString().split('T')[0];
    const tipo = this.mapaDisponibilidad.get(key);
    
    if (!tipo) return 'Disponible';
    if (tipo === 'RESERVA_CONFIRMADA') return 'Ya reservado (Confirmado)';
    if (tipo === 'RESERVA_PENDIENTE') return 'Reserva en proceso';
    if (tipo === 'BLOQUEO_MANUAL') return 'No disponible (Bloqueado por el anfitrión)';
    return 'No disponible';
  }

  onDateSelect() {
    if (this.rangoFechas && this.rangoFechas[0] && this.rangoFechas[1]) {
      this.fechaInicio = this.rangoFechas[0];
      this.fechaFin = this.rangoFechas[1];
      this.calcularPrecio();
    } else {
      this.fechaInicio = null;
      this.fechaFin = null;
      this.precioTotal = 0;
    }
  }

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

  getToday(): Date {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return today;
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