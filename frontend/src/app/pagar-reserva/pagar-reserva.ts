import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ButtonModule } from 'primeng/button';
import { MessageService } from 'primeng/api';
import { environment } from '../../environments/environment';

interface PagoRequestDTO {
  reservaId: number;
  monto: number;
  metodoPago: string;
}

interface PagoResponseDTO {
  id: number;
  reservaId: number;
  monto: number;
  metodo: string;
  estado: string;
  fecha: string;
}

@Component({
  selector: 'app-pagar-reserva',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule, ButtonModule],
  templateUrl: './pagar-reserva.html',
  styleUrl: './pagar-reserva.scss'
})
export class PagarReserva implements OnInit {
  private readonly API_URL = `${environment.apiUrl}/api`;

  // Datos de la reserva
  reservaId: number = 0;
  monto: number = 0;
  alojamiento: string = '';
  fechaInicio: string = '';
  fechaFin: string = '';

  // Formulario de pago
  metodoPago: 'TARJETA' | 'PSE' | 'EFECTIVO' = 'TARJETA';
  nombreTitular: string = '';
  numeroTarjeta: string = '';
  vencimiento: string = '';
  cvv: string = '';

  // Estado
  procesando: boolean = false;
  pagado: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private messageService: MessageService
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.reservaId = Number(params['reservaId']) || 0;
      this.monto = Number(params['monto']) || 0;
      this.alojamiento = params['alojamiento'] || 'Alojamiento';
      this.fechaInicio = params['fechaInicio'] || '';
      this.fechaFin = params['fechaFin'] || '';

      if (!this.reservaId) {
        this.router.navigate(['/mi-cuenta']);
      }
    });
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '';
    const d = new Date(dateStr + 'T00:00:00');
    return d.toLocaleDateString('es-CO', { day: '2-digit', month: 'long', year: 'numeric' });
  }

  formatCard(value: string): string {
    return value.replace(/\D/g, '').substring(0, 16).replace(/(.{4})/g, '$1 ').trim();
  }

  onCardInput(event: any) {
    const raw = event.target.value.replace(/\s/g, '').substring(0, 16);
    this.numeroTarjeta = raw.replace(/(.{4})/g, '$1 ').trim();
  }

  onVencimientoInput(event: any) {
    let val = event.target.value.replace(/\D/g, '').substring(0, 4);
    if (val.length > 2) val = val.substring(0, 2) + '/' + val.substring(2);
    this.vencimiento = val;
  }

  isFormValid(): boolean {
    if (this.metodoPago === 'TARJETA') {
      const rawCard = this.numeroTarjeta.replace(/\s/g, '');
      return (
        this.nombreTitular.trim().length >= 3 &&
        rawCard.length === 16 &&
        this.vencimiento.length === 5 &&
        this.cvv.length >= 3
      );
    }
    return true; // PSE / Efectivo — validación simplificada
  }

  pagar() {
    if (!this.isFormValid()) {
      this.messageService.add({ severity: 'warn', summary: 'Formulario incompleto', detail: 'Por favor completa todos los campos del pago.' });
      return;
    }

    this.procesando = true;

    const dto: PagoRequestDTO = {
      reservaId: this.reservaId,
      monto: this.monto,
      metodoPago: this.metodoPago
    };

    this.http.post<PagoResponseDTO>(`${this.API_URL}/pagos`, dto).subscribe({
      next: (pago) => {
        this.procesando = false;
        this.pagado = true;
        this.messageService.add({
          severity: 'success',
          summary: '¡Pago exitoso!',
          detail: `Tu pago de $${this.monto.toLocaleString('es-CO')} COP fue procesado. Tu reserva está CONFIRMADA.`
        });

        setTimeout(() => {
          this.router.navigate(['/mi-cuenta']);
        }, 3000);
      },
      error: (err) => {
        this.procesando = false;
        console.error('Error en pago:', err);
        const msg = err.error?.message || err.message || 'Error al procesar el pago. Intenta nuevamente.';
        this.messageService.add({ severity: 'error', summary: 'Error de pago', detail: msg });
      }
    });
  }

  cancelar() {
    this.router.navigate(['/mi-cuenta']);
  }
}
