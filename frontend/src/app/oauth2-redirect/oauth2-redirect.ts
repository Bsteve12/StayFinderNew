import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
    selector: 'app-oauth2-redirect',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div style="display: flex; justify-content: center; align-items: center; height: 100vh; flex-direction: column; font-family: sans-serif;">
      <i class="pi pi-spin pi-spinner" style="font-size: 2rem; color: #7c3aed; margin-bottom: 1rem;"></i>
      <h3>Completando inicio de sesión...</h3>
    </div>
  `
})
export class Oauth2Redirect implements OnInit {
    constructor(private route: ActivatedRoute, private router: Router) { }

    ngOnInit() {
        this.route.queryParams.subscribe(params => {
            const token = params['token'];
            if (token) {
                // Guardar el token en localStorage igual que en el login normal
                localStorage.setItem('token', token);

                // Extraemos manualmente la informacion del payload para simular la respuesa del authService
                try {
                    const parts = token.split('.');
                    if (parts.length >= 2) {
                        const payload = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')));
                        const user = {
                            usuarioId: payload.usuarioId || null,
                            id: payload.usuarioId || null,
                            email: payload.sub || null,
                            role: payload.rol ? payload.rol.toUpperCase() : undefined
                        };
                        localStorage.setItem('user', JSON.stringify(user));
                        if (user.role) {
                            localStorage.setItem('role', user.role);
                        }
                    }
                } catch (e) {
                    console.error("Error parseando el JWT token", e);
                }

                // Forzamos un reload recargando todo el estado de la aplicación mediante ruteo
                // Ya que AuthService lee de localStorage al iniciarse, se autologueará exitosamente
                window.location.href = '/inicio';
            } else {
                // Redirigir a login si falla
                this.router.navigate(['/login']);
            }
        });
    }
}
