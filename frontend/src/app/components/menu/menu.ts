import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core'; // 👈 Agregado OnChanges y SimpleChanges
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MenuModule } from 'primeng/menu';
import { ButtonModule } from 'primeng/button';
import { MenuItem } from 'primeng/api';
import { MessageService } from 'primeng/api';


@Component({
  selector: 'app-menu',
  imports: [
    CommonModule,
    MenuModule,
    ButtonModule
  ],
  templateUrl: './menu.html',
  styleUrls: ['./menu.scss'],
})
export class Menu implements OnInit, OnChanges { // 👈 Implementación de OnChanges
  @Input() isAuthenticated: boolean = false;
  @Input() userRole: 'CLIENT' | 'OWNER' | 'ADMIN' | null = null;
  @Input() userName: string = '';
  @Input() userPhoto: string = '';


  menuItems: MenuItem[] = [];


  constructor(private router: Router, private messageService: MessageService) { }


  ngOnInit() {
    this.setupMenuItems();
  }


  // 👇 Nuevo: Detecta cambios en @Input()
  ngOnChanges(changes: SimpleChanges) {
    if (changes['isAuthenticated'] || changes['userRole']) {
      this.setupMenuItems();
    }
  }


  setupMenuItems() {
    if (!this.isAuthenticated) {
      // Usuario NO autenticado
      this.menuItems = [
        {
          label: 'Iniciar sesión',
          icon: 'pi pi-sign-in',
          command: () => {
            this.goToLogin();
          }
        },
        {
          label: 'Registrarse',
          icon: 'pi pi-user-plus',
          command: () => {
            this.goToRegister();
          }
        },
        {
          separator: true
        },
        {
          label: 'Ayuda y Soporte',
          icon: 'pi pi-question-circle',
          command: () => {
            this.goToSupport();
          }
        }
      ];
    } else {
      // Usuario AUTENTICADO
      this.menuItems = [
        {
          label: 'Mi cuenta',
          icon: 'pi pi-user',
          command: () => {
            this.goToAccount();
          }
        }
      ];


      // Agregar opciones según el rol
      if (this.userRole === 'CLIENT') {
        this.menuItems.push(
          {
            label: 'Mis Reservas',
            icon: 'pi pi-calendar',
            command: () => {
              this.router.navigate(['mi-cuenta']);
            }
          },
          {
            label: 'Favoritos',
            icon: 'pi pi-heart',
            command: () => {
              this.router.navigate(['mi-cuenta']);
            }
          }
        );
      } else if (this.userRole === 'OWNER') {
        this.menuItems.push(
          {
            label: 'Dashboard Anfitrión',
            icon: 'pi pi-chart-line',
            command: () => {
              this.router.navigate(['/anfitrion']);
            }
          }
        );
      } else if (this.userRole === 'ADMIN') {
        this.menuItems.push(
          {
            label: 'Panel Admin',
            icon: 'pi pi-shield',
            command: () => {
              this.router.navigate(['/administrador']);
            }
          },
          {
            label: 'Gestionar Usuarios',
            icon: 'pi pi-users',
            command: () => {
              this.router.navigate(['/administrador']);
            }
          },
          {
            label: 'Solicitudes',
            icon: 'pi pi-inbox',
            command: () => {
              this.router.navigate(['/administrador']);
            }
          }
        );
      }


      // Opciones comunes para todos los autenticados
      this.menuItems.push(
        {
          separator: true
        },
        {
          label: 'Ayuda y Soporte',
          icon: 'pi pi-question-circle',
          command: () => {
            this.goToSupport();
          }
        },
        {
          separator: true
        },
        {
          label: 'Cerrar sesión',
          icon: 'pi pi-sign-out',
          styleClass: 'logout-item',
          command: () => {
            this.logout();
          }
        }
      );
    }
  }


  goToLogin() {
    this.router.navigate(['/login']);
  }


  goToRegister() {
    this.router.navigate(['/register']);
  }


  goToAccount() {
    // Redirigir según el rol
    if (this.userRole === 'CLIENT') {
      this.router.navigate(['/mi-cuenta']);
    } else if (this.userRole === 'OWNER') {
      this.router.navigate(['/anfitrion']);
    } else if (this.userRole === 'ADMIN') {
      this.router.navigate(['/administrador']);
    }
  }


  goToSupport() {
    this.router.navigate(['/soporte']);
  }


  logout() {
    // Lógica de logout
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('role');


    // Redirigir al login
    this.router.navigate(['/login']);


    // Opcional: Mostrar mensaje
    this.messageService.add({ severity: 'success', summary: 'Éxito', detail: 'Sesión cerrada exitosamente' });
  }


}
