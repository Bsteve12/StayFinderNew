import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'inicio', pathMatch: 'full' },
  { path: 'inicio', loadComponent: () => import('./inicio/inicio').then(m => m.Inicio) },
  { path: 'soporte', loadComponent: () => import('./soporte/soporte').then(m => m.Soporte) },
  { path: 'detalle/:id', loadComponent: () => import('./detalle/detalle').then(m => m.Detalle) },
  { path: 'login', loadComponent: () => import('./login/login').then(m => m.Login) },
  { path: 'register', loadComponent: () => import('./register/register').then(m => m.Register) },
  { path: 'forgot-password', loadComponent: () => import('./password/password').then(m => m.Password) },
  { path: 'mi-cuenta', loadComponent: () => import('./mi-cuenta/mi-cuenta').then(m => m.MiCuenta) },
  { path: 'anfitrion', loadComponent: () => import('./anfitrion/anfitrion').then(m => m.Anfitrion) },
  { path: 'administrador', loadComponent: () => import('./administrador/administrador').then(m => m.Administrador) },
  { path: 'convertirse-anfitrion', loadComponent: () => import('./convertirse-anfitrion/convertirse-anfitrion').then(m => m.ConvertirseAnfitrion) },
  { path: 'inicio-mi-cuenta', loadComponent: () => import('./inicio-mi-cuenta/inicio-mi-cuenta').then(m => m.InicioMiCuenta) },
  { path: 'reset-password', loadComponent: () => import('./reset-password/reset-password').then(m => m.ResetPassword) },
  { path: 'oauth2/redirect', loadComponent: () => import('./oauth2-redirect/oauth2-redirect').then(m => m.Oauth2Redirect) },
  { path: 'pagar-reserva', loadComponent: () => import('./pagar-reserva/pagar-reserva').then(m => m.PagarReserva) },
];
