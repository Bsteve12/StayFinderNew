import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CarouselModule } from 'primeng/carousel';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from "@angular/router";
import { Header } from "../components/header/header";
import { AlojamientosService } from '../services/alojamientos';

@Component({
  selector: 'app-inicio-mi-cuenta',
  standalone: true,
  imports: [
    CommonModule,
    CarouselModule,
    ButtonModule,
    InputTextModule,
    InputNumberModule,
    FormsModule,
    MatButtonModule,
    RouterLink,
    Header
  ],
  templateUrl: './inicio-mi-cuenta.html',
  styleUrls: ['./inicio-mi-cuenta.scss']
})
export class InicioMiCuenta {

  destinations: any[] = [];
  testimonials: any[] = [];

  constructor(private alojamientosService: AlojamientosService) {
    this.getAlojamientosActivos();
  }

  getAlojamientosActivos() {
    this.alojamientosService.obtenerAlojamientosActivos().subscribe({
      next: (data) => this.destinations = data,
      error: (error) => console.error('Error al obtener alojamientos activos:', error)
    });
  }

  responsiveOptions = [
    { breakpoint: '1400px', numVisible: 4, numScroll: 1 },
    { breakpoint: '1200px', numVisible: 3, numScroll: 1 },
    { breakpoint: '768px',  numVisible: 2, numScroll: 1 },
    { breakpoint: '560px',  numVisible: 1, numScroll: 1 }
  ];

  toggleFavorite(destination: any) {
    destination.favorite = !destination.favorite;
  }
}
