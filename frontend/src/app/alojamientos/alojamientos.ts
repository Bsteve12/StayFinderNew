import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AlojamientosService } from '../services/alojamientos';
import { AuthService, User } from '../services/auth.service';
import { AlojamientoResponseDTO } from '../models/alojamiento.model';
import { Header } from '../components/header/header';

@Component({
  selector: 'app-alojamientos',
  standalone: true,
  imports: [CommonModule, Header],
  templateUrl: './alojamientos.html',
  styleUrl: './alojamientos.scss',
})
export class Alojamientos implements OnInit {
  alojamientos: AlojamientoResponseDTO[] = [];
  currentUser: User | null = null;
  loading: boolean = true;

  constructor(
    private alojamientosService: AlojamientosService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.authService.currentUser$.subscribe((user) => {
      this.currentUser = user;
      if (this.currentUser && this.currentUser.id) {
        this.cargarAlojamientos();
      } else {
        this.loading = false;
      }
    });
  }

  cargarAlojamientos(): void {
    if (!this.currentUser || !this.currentUser.id) return;

    this.loading = true;
    this.alojamientosService.obtenerAlojamientosDeOwner(this.currentUser.id).subscribe({
      next: (data) => {
        this.alojamientos = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error cargando alojamientos:', err);
        this.loading = false;
      }
    });
  }

  crearNuevo(): void {
    this.router.navigate(['/alojamientos/nuevo']);
  }

  editar(id: number): void {
    this.router.navigate(['/alojamientos/editar', id]);
  }

  eliminar(id: number): void {
    if (!this.currentUser || !this.currentUser.id) return;
    if (confirm('¿Estás seguro de que deseas eliminar este alojamiento? Esta acción no se puede deshacer.')) {
      this.alojamientosService.eliminarAlojamiento(id, this.currentUser.id).subscribe({
        next: () => {
          // Remover de la lista localmente
          this.alojamientos = this.alojamientos.filter(a => a.id !== id);
          alert('Alojamiento eliminado exitosamente.');
        },
        error: (err) => {
          console.error('Error eliminando alojamiento:', err);
          alert('Error al eliminar el alojamiento.');
        }
      });
    }
  }

  irInicio(): void {
    this.router.navigate(['/']);
  }
}
