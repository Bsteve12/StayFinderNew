import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, InputTextModule, ButtonModule, ToastModule],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.scss',
  providers: [MessageService]
})
export class ResetPassword implements OnInit {
  resetForm: FormGroup;
  token: string = '';
  loading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private messageService: MessageService
  ) {
    this.resetForm = this.fb.group({
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]]
    }, { validator: this.passwordMatchValidator });
  }

  ngOnInit() {
    // Capturamos el token de la URL: /reset-password?token=xxxx
    this.token = this.route.snapshot.queryParamMap.get('token') || '';
    if (!this.token) {
      alert('Token no encontrado. Por favor, solicita un nuevo correo de recuperación.');
      this.router.navigate(['/forgot-password']);
    }
  }

  passwordMatchValidator(g: FormGroup) {
    return g.get('password')?.value === g.get('confirmPassword')?.value
      ? null : { 'mismatch': true };
  }

  onSubmit() {
    if (this.resetForm.valid && this.token) {
      this.loading = true;
      const nuevaPassword = this.resetForm.get('password')?.value;

      // Llamamos al servicio (asegúrate de tener este método en AuthService)
      this.authService.resetPassword(this.token, nuevaPassword).subscribe({
        next: (res) => {
          this.messageService.add({
            severity: 'success',
            summary: '¡Éxito!',
            detail: 'Contraseña nueva creada exitosamente'
          });
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 2000);
        },
        error: (err) => {
          // El usuario solicitó explícitamente ignorar el error visual 
          // y mostrar el mensaje de éxito para forzar la navegación al login.
          console.error("Error real del backend:", err);

          this.messageService.add({
            severity: 'success',
            summary: '¡Éxito!',
            detail: 'Contraseña nueva creada exitosamente'
          });
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 2000);
        }
      });
    }
  }

  get isPasswordInvalid() {
    const control = this.resetForm.get('password');
    return control?.invalid && control?.touched;
  }

  get isMismatch() {
    return this.resetForm.hasError('mismatch') && this.resetForm.get('confirmPassword')?.touched;
  }
}
