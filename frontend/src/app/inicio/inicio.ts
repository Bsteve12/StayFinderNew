import { Component, inject, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CarouselModule } from 'primeng/carousel';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { FormsModule, FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { provideNativeDateAdapter, MAT_DATE_FORMATS, MAT_NATIVE_DATE_FORMATS } from '@angular/material/core';
import { RouterLink } from "@angular/router";
import { Header } from "../components/header/header";
import { AlojamientosService } from '../services/alojamientos';


interface Destination {
  id: number;
  nombre: string;
  direccion: string;
  precio: number;
  descripcion: string;
  imagenes: { id: number; url: string; alojamientoId: number }[];
  rating?: number;
  favorite?: boolean;
}


interface Testimonial {
  id: number;
  name: string;
  avatar: string;
  comment: string;
  rating: number;
  date: string;
}


@Component({
  selector: 'app-inicio',
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
  providers: [provideNativeDateAdapter()],
  templateUrl: './inicio.html',
  styleUrl: './inicio.scss',
})
export class Inicio {


  destinations: Destination[] = [];


  constructor(private alojamientosService: AlojamientosService) {
    this.getAlojamientosActivos();
  }


  getAlojamientosActivos() {
    this.alojamientosService.obtenerAlojamientosActivos().subscribe({
      next: (data) => {
        console.log(data)
        this.destinations = data;
      },
      error: (error) => {
        console.error('Error al obtener alojamientos activos:', error);
      }
    });
  }






  // Testimonios
  testimonials: Testimonial[] = [
    {
      id: 1,
      name: 'María González',
      avatar: 'https://i.pravatar.cc/150?img=1',
      comment: 'Excelente experiencia, el lugar superó mis expectativas. La ubicación era perfecta y la atención al cliente fue increíble.',
      rating: 5,
      date: 'Hace 2 semanas'
    },
    {
      id: 2,
      name: 'Carlos Rodríguez',
      avatar: 'https://i.pravatar.cc/150?img=3',
      comment: 'Muy recomendado, todo estuvo perfecto. El apartamento estaba impecable y tenía todas las comodidades necesarias.',
      rating: 5,
      date: 'Hace 1 mes'
    },
    {
      id: 3,
      name: 'Ana Martínez',
      avatar: 'https://i.pravatar.cc/150?img=5',
      comment: 'Un lugar hermoso y acogedor. Definitivamente volveré. La vista era espectacular y la zona muy tranquila.',
      rating: 5,
      date: 'Hace 3 semanas'
    },
    {
      id: 4,
      name: 'Juan Pérez',
      avatar: 'https://i.pravatar.cc/150?img=7',
      comment: 'Perfecta para unas vacaciones en familia. Todos disfrutamos muchísimo nuestra estadía.',
      rating: 4,
      date: 'Hace 1 semana'
    },
    {
      id: 5,
      name: 'Laura Sánchez',
      avatar: 'https://i.pravatar.cc/150?img=9',
      comment: 'Increíble atención y un lugar mágico. Sin duda la mejor opción para descansar y relajarse.',
      rating: 5,
      date: 'Hace 4 días'
    }
  ];


  // Opciones del carrusel
  responsiveOptions = [
    {
      breakpoint: '1400px',
      numVisible: 4,
      numScroll: 1
    },
    {
      breakpoint: '1200px',
      numVisible: 3,
      numScroll: 1
    },
    {
      breakpoint: '768px',
      numVisible: 2,
      numScroll: 1
    },
    {
      breakpoint: '560px',
      numVisible: 1,
      numScroll: 1
    }
  ];


  toggleFavorite(destination: Destination) {
    destination.favorite = !destination.favorite;
  }




}
