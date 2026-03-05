import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AlojamientosService } from '../../services/alojamientos';
import { AuthService, User } from '../../services/auth.service';
import { AlojamientoRequestDTO } from '../../models/alojamiento.model';
import { Header } from '../../components/header/header';

@Component({
  selector: 'app-alojamiento-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, Header],
  templateUrl: './alojamiento-form.html',
  styleUrl: './alojamiento-form.scss'
})
export class AlojamientoForm implements OnInit {
  alojamientoForm: FormGroup;
  isEditMode = false;
  alojamientoId: number | null = null;
  currentUser: User | null = null;
  loading = false;
  submitting = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private alojamientosService: AlojamientosService,
    private authService: AuthService
  ) {
    this.alojamientoForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(5)]],
      direccion: ['', [Validators.required, Validators.minLength(5)]],
      precio: ['', [Validators.required, Validators.min(1)]],
      descripcion: ['', [Validators.required, Validators.minLength(10)]],
      capacidadMaxima: ['', [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    this.authService.currentUser$.subscribe((user) => {
      this.currentUser = user;
    });

    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.isEditMode = true;
        this.alojamientoId = +idParam;
        this.cargarAlojamiento(this.alojamientoId);
      }
    });
  }

  cargarAlojamiento(id: number): void {
    this.loading = true;
    this.alojamientosService.obtenerAlojamientoPorId(id).subscribe({
      next: (data) => {
        this.alojamientoForm.patchValue({
          nombre: data.nombre,
          direccion: data.direccion,
          precio: data.precio,
          descripcion: data.descripcion,
          capacidadMaxima: data.capacidadMaxima
        });
        this.loading = false;
      },
      error: (err) => {
        console.error('Error cargando alojamiento:', err);
        this.errorMessage = 'No se pudo cargar la información del alojamiento.';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.alojamientoForm.invalid) {
      this.alojamientoForm.markAllAsTouched();
      return;
    }
    if (!this.currentUser || !this.currentUser.id) {
      this.errorMessage = 'Sesión expirada o usuario no válido. Por favor inicia sesión de nuevo.';
      return;
    }

    this.submitting = true;
    this.errorMessage = '';
    const formData: AlojamientoRequestDTO = this.alojamientoForm.value;

    if (this.isEditMode && this.alojamientoId) {
      this.alojamientosService.editarAlojamiento(this.alojamientoId, formData, this.currentUser.id).subscribe({
        next: () => {
          alert('Alojamiento actualizado con éxito.');
          this.router.navigate(['/alojamientos']);
        },
        error: (err) => {
          console.error(err);
          this.errorMessage = 'Ocurrió un error al actualizar el alojamiento.';
          this.submitting = false;
        }
      });
    } else {
      this.alojamientosService.crearAlojamiento(formData, this.currentUser.id).subscribe({
        next: (response) => {
          alert('Alojamiento creado con éxito.');
          // Redirecting back to the list. Images and amenities setup can happen next.
          this.router.navigate(['/alojamientos']);
        },
        error: (err) => {
          console.error(err);
          this.errorMessage = 'Ocurrió un error al crear el alojamiento.';
          this.submitting = false;
        }
      });
    }
  }

  cancelar(): void {
    this.router.navigate(['/alojamientos']);
  }
}
