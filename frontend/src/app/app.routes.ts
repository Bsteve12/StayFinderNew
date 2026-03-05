import { Routes } from '@angular/router';
import { Soporte } from './soporte/soporte';
import { Inicio } from './inicio/inicio';
import { Detalle } from './detalle/detalle';
import { Login } from './login/login';
import { Register } from './register/register';
import { Password } from './password/password';
import { MiCuenta } from './mi-cuenta/mi-cuenta';
import { Anfitrion } from './anfitrion/anfitrion';
import { Administrador } from './administrador/administrador';
import { ConvertirseAnfitrion } from './convertirse-anfitrion/convertirse-anfitrion';
import { InicioMiCuenta } from './inicio-mi-cuenta/inicio-mi-cuenta';
import { ResetPassword } from './reset-password/reset-password';
import { Oauth2Redirect } from './oauth2-redirect/oauth2-redirect';

import { Alojamientos } from './alojamientos/alojamientos';
import { AlojamientoForm } from './alojamientos/alojamiento-form/alojamiento-form';

export const routes: Routes = [
  { path: '', redirectTo: 'inicio', pathMatch: 'full' },
  { path: 'inicio', component: Inicio },
  { path: 'soporte', component: Soporte },
  { path: 'detalle/:id', component: Detalle },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'forgot-password', component: Password },
  { path: 'mi-cuenta', component: MiCuenta },
  { path: 'anfitrion', component: Anfitrion },
  { path: 'administrador', component: Administrador },
  { path: 'convertirse-anfitrion', component: ConvertirseAnfitrion },
  { path: 'inicio-mi-cuenta', component: InicioMiCuenta },
  { path: 'reset-password', component: ResetPassword },
  { path: 'oauth2/redirect', component: Oauth2Redirect },
  { path: 'alojamientos', component: Alojamientos },
  { path: 'alojamientos/nuevo', component: AlojamientoForm },
  { path: 'alojamientos/editar/:id', component: AlojamientoForm },
];
