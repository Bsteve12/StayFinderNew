import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { AuthService } from '../services/auth.service'; // Asegúrate de que la ruta sea correcta

@Component({
  selector: 'app-password',
  standalone: true, // Agregado standalone
  imports: [CommonModule, FormsModule, ReactiveFormsModule, InputTextModule, ButtonModule],
  templateUrl: './password.html',
  styleUrl: './password.scss',
})
export class Password {
  forgotPasswordForm: FormGroup;
  emailSent: boolean = false;
  loading: boolean = false; // Para deshabilitar el botón mientras carga

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService // INYECTADO
  ) {
    this.forgotPasswordForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  onSubmit() {
    if (this.forgotPasswordForm.valid) {
      this.loading = true;
      const emailValue = this.forgotPasswordForm.get('email')?.value;

      // LLAMADA REAL AL BACKEND
      this.authService.forgotPassword(emailValue).subscribe({
        next: (response) => {
          this.emailSent = true;
          this.loading = false;
          console.log('Solicitud enviada al backend');
          // No redirigimos de inmediato para que el usuario vea el mensaje de éxito
        },
        error: (err) => {
          this.loading = false;
          alert('Hubo un error: ' + (err.error?.message || 'El correo no está registrado'));
        }
      });
    } else {
      this.email?.markAsTouched();
    }
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }

  get email() { return this.forgotPasswordForm.get('email'); }
  get isEmailInvalid() { return this.email?.invalid && this.email?.touched; }

  getEmailError(): string {
    if (this.email?.hasError('required')) return 'El email es requerido';
    if (this.email?.hasError('email')) return 'Email inválido';
    return '';
  }
}
