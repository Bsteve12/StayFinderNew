// src/app/components/header/header.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from "@angular/router";
import { CarouselModule } from 'primeng/carousel';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { DatePickerDialog } from '../date-picker-dialog/date-picker-dialog';
import { Menu } from "../menu/menu";
import { AuthService, User } from '../../services/auth.service';

@Component({
  selector: 'app-header',
  imports: [
    CommonModule,
    CarouselModule,
    ButtonModule,
    InputTextModule,
    InputNumberModule,
    FormsModule,
    MatButtonModule,
    RouterModule,
    Menu
  ],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header implements OnInit {
  isAuthenticated = false;
  userRole: 'CLIENT' | 'OWNER' | 'ADMIN' | null = null;
  userName = '';
  userPhoto = '';

  searchLocation = '';
  checkInDate: Date | null = null;
  checkOutDate: Date | null = null;
  guests = 1;

  constructor(
    private router: Router,
    private dialog: MatDialog,
    private auth: AuthService
  ) {}

  ngOnInit() {
    this.auth.isAuthenticated$.subscribe(isAuth => {
      this.isAuthenticated = isAuth;
    });
    this.auth.currentUser$.subscribe((user: User | null) => {
      this.userRole = (user?.role as 'CLIENT' | 'OWNER' | 'ADMIN') || null;
      this.userName = user?.nombre || 'Usuario';
      '';
    });
  }

  onBecomeHost() {
    if (!this.isAuthenticated) {
      alert('Debes iniciar sesión para convertirte en anfitrión.');
      this.router.navigate(['/login'], { queryParams: { returnUrl: '/convertirse-anfitrion' } });
    } else {
      this.router.navigate(['/convertirse-anfitrion']);
    }
  }

  openCheckInDialog() {
    const dialogRef = this.dialog.open(DatePickerDialog, {
      data: { selectedDate: this.checkInDate, title: 'Selecciona fecha de Check-in' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.checkInDate = result;
      }
    });
  }

  openCheckOutDialog() {
    const dialogRef = this.dialog.open(DatePickerDialog, {
      data: { selectedDate: this.checkOutDate, title: 'Selecciona fecha de Check-out' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.checkOutDate = result;
      }
    });
  }

  formatDate(date: Date | null): string {
    if (!date) return 'Agrega fecha';
    return date.toLocaleDateString('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric' });
  }

  onSearch() {
    this.router.navigate(['/buscar'], {
      queryParams: {
        ubicacion: this.searchLocation,
        checkIn: this.checkInDate?.toISOString(),
        checkOut: this.checkOutDate?.toISOString(),
        guests: this.guests
      }
    });
  }
}
