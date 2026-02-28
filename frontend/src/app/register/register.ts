import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDialog, MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { provideNativeDateAdapter, MAT_DATE_FORMATS, MAT_NATIVE_DATE_FORMATS } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { UsuarioService, CreateUserDTO } from '../services/usuario.service';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    InputTextModule,
    ButtonModule,
    MatButtonModule,
    ToastModule
  ],
  providers: [provideNativeDateAdapter(), MessageService],
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class Register {
  registerForm: FormGroup;
  dialog = inject(MatDialog);

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private usuarioService: UsuarioService,
    private messageService: MessageService
  ) {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      phone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      birthdate: [null, [Validators.required]],
    });
  }

  onRegister() {
    if (this.registerForm.valid) {
      // Adaptar el formato al DTO del backend
      const formValue = this.registerForm.value;
      const newUser: CreateUserDTO = {
        nombre: formValue.username,
        correo: formValue.email,
        telefono: formValue.phone,
        fechaNacimiento: this.formatDateForBackend(formValue.birthdate),
        usuarioId: Math.floor(Math.random() * 9000000000), // Generar ID temporal
        contrasena: formValue.password,
      };

      this.usuarioService.register(newUser).subscribe({
        next: (response) => {
          this.messageService.add({
            severity: 'success',
            summary: 'Registro exitoso',
            detail: 'Tu cuenta ha sido creada correctamente.',
          });
          setTimeout(() => this.router.navigate(['/login']), 1500);
        },
        error: (err) => {
          console.error('Error en registro:', err);
          this.messageService.add({
            severity: 'error',
            summary: 'Error en registro',
            detail: err?.error?.message || 'No se pudo crear el usuario.',
          });
        },
      });
    } else {
      Object.keys(this.registerForm.controls).forEach(key => {
        this.registerForm.get(key)?.markAsTouched();
      });
    }
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }

  openBirthdateDialog() {
    const dialogRef = this.dialog.open(BirthdateDatePickerDialog, {
      data: { selectedDate: this.birthdate?.value, title: 'Selecciona tu fecha de nacimiento' },
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.registerForm.patchValue({ birthdate: result });
        this.birthdate?.markAsTouched();
      }
    });
  }

  formatDate(date: Date | null): string {
    if (!date) return 'Fecha de nacimiento';
    return new Date(date).toLocaleDateString('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric' });
  }

  // Formato de fecha para backend (yyyy-MM-dd)
  formatDateForBackend(date: Date): string {
    const d = new Date(date);
    return d.toISOString().split('T')[0];
  }

  // Getters
  get username() { return this.registerForm.get('username'); }
  get email() { return this.registerForm.get('email'); }
  get password() { return this.registerForm.get('password'); }
  get phone() { return this.registerForm.get('phone'); }
  get birthdate() { return this.registerForm.get('birthdate'); }

  get isUsernameInvalid() { return this.username?.invalid && this.username?.touched; }
  get isEmailInvalid() { return this.email?.invalid && this.email?.touched; }
  get isPasswordInvalid() { return this.password?.invalid && this.password?.touched; }
  get isPhoneInvalid() { return this.phone?.invalid && this.phone?.touched; }
  get isBirthdateInvalid() { return this.birthdate?.invalid && this.birthdate?.touched; }

  getUsernameError() { if (this.username?.hasError('required')) return 'El usuario es requerido'; if (this.username?.hasError('minlength')) return 'Mínimo 3 caracteres'; return ''; }
  getEmailError() { if (this.email?.hasError('required')) return 'El email es requerido'; if (this.email?.hasError('email')) return 'Email inválido'; return ''; }
  getPasswordError() { if (this.password?.hasError('required')) return 'La contraseña es requerida'; if (this.password?.hasError('minlength')) return 'Mínimo 8 caracteres'; return ''; }
  getPhoneError() { if (this.phone?.hasError('required')) return 'El teléfono es requerido'; if (this.phone?.hasError('pattern')) return 'Debe tener 10 dígitos'; return ''; }
  getBirthdateError() { if (this.birthdate?.hasError('required')) return 'La fecha es requerida'; return ''; }
  getMaxDate(): Date { const today = new Date(); today.setFullYear(today.getFullYear() - 18); return today; }
}

// Dialog del DatePicker
@Component({
  selector: 'birthdate-date-picker-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDatepickerModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule,
  ],
  providers: [
    provideNativeDateAdapter(),
    { provide: MAT_DATE_FORMATS, useValue: MAT_NATIVE_DATE_FORMATS },
  ],
  template: `
    <div class="dialog-container">
      <h2 mat-dialog-title>{{ data.title }}</h2>
      <mat-dialog-content>
        <mat-form-field>
          <mat-label>Selecciona una fecha</mat-label>
          <input matInput [matDatepicker]="picker" [formControl]="date" [max]="maxDate">
          <mat-datepicker-toggle matIconSuffix [for]="picker"></mat-datepicker-toggle>
          <mat-datepicker #picker></mat-datepicker>
        </mat-form-field>
      </mat-dialog-content>
      <mat-dialog-actions align="end">
        <button mat-button (click)="onCancel()">Cancelar</button>
        <button mat-raised-button color="primary" (click)="onConfirm()">Confirmar</button>
      </mat-dialog-actions>
    </div>
  `,
  styles: [`
    .dialog-container {
      padding: 20px;
      min-width: 300px;
    }
    mat-form-field {
      width: 100%;
      margin-top: 16px;
    }
    mat-dialog-actions {
      margin-top: 24px;
      padding-top: 16px;
    }
  `]
})
export class BirthdateDatePickerDialog {
  dialogRef = inject<MatDialogRef<BirthdateDatePickerDialog>>(MatDialogRef<BirthdateDatePickerDialog>);
  data = inject(MAT_DIALOG_DATA);
  date = new FormControl(new Date());
  maxDate = new Date();

  constructor() {
    this.maxDate.setFullYear(this.maxDate.getFullYear() - 18);
    if (this.data.selectedDate) this.date.setValue(this.data.selectedDate);
    else this.date.setValue(this.maxDate);
  }

  onCancel() { this.dialogRef.close(); }
  onConfirm() { this.dialogRef.close(this.date.value); }
}
