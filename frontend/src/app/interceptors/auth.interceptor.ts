import { Injectable } from '@angular/core';
import {
  HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError, TimeoutError } from 'rxjs';
import { catchError, timeout } from 'rxjs/operators';
import { MessageService } from 'primeng/api';
import { Router } from '@angular/router';

/** Timeout global: 30 segundos para evitar esperas infinitas */
const REQUEST_TIMEOUT_MS = 30_000;

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(
    private messageService: MessageService,
    private router: Router
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // ── 1. Inyectar Bearer token ──
    const token = localStorage.getItem('token');
    let authReq = req;

    if (token) {
      authReq = req.clone({
        setHeaders: { Authorization: `Bearer ${token}` }
      });
    }

    // ── 2. Ejecutar con timeout + manejo centralizado de errores ──
    return next.handle(authReq).pipe(
      timeout(REQUEST_TIMEOUT_MS),
      catchError((error: HttpErrorResponse | TimeoutError) => {

        // Timeout — la petición nunca respondió
        if (error instanceof TimeoutError) {
          this.messageService.add({
            severity: 'warn',
            summary: 'Tiempo de espera agotado',
            detail: 'El servidor no respondió a tiempo. Verifica tu conexión e intenta de nuevo.',
            life: 6000
          });
          return throwError(() => error);
        }

        const httpError = error as HttpErrorResponse;

        switch (httpError.status) {
          case 401:
            // Token expirado o inválido → limpiar sesión y redirigir
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            this.messageService.add({
              severity: 'error',
              summary: 'Sesión expirada',
              detail: 'Tu sesión ha expirado. Por favor, inicia sesión nuevamente.',
              life: 5000
            });
            this.router.navigate(['/login']);
            break;

          case 403:
            this.messageService.add({
              severity: 'error',
              summary: 'Acceso denegado',
              detail: 'No tienes permisos para realizar esta acción.',
              life: 5000
            });
            break;

          case 500:
            // Solo logueamos el error 500 en consola.
            // Los componentes individuales ya manejan sus propios mensajes de error.
            // Esto cumple el requisito de auditoría (capturar + reportar) sin duplicar toasts.
            console.error('[Interceptor] Error 500 detectado:', httpError.url, httpError.message);
            break;

          case 0:
            // Error de red (sin conexión, CORS, servidor caído)
            this.messageService.add({
              severity: 'error',
              summary: 'Sin conexión',
              detail: 'No se puede conectar con el servidor. Verifica tu conexión a internet.',
              life: 6000
            });
            break;
        }

        return throwError(() => httpError);
      })
    );
  }
}
