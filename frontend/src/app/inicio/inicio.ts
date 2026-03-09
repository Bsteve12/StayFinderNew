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
import { RouterLink, Router } from "@angular/router";
import { Header } from "../components/header/header";
import { AlojamientosService } from '../services/alojamientos';
import { DatePickerDialog } from '../components/date-picker-dialog/date-picker-dialog';


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
  groupedDestinations: { [key: string]: Destination[] } = {};

  // Buscador
  searchLocation: string = '';
  checkInDate: Date | null = null;
  checkOutDate: Date | null = null;
  guests: number = 1;

  constructor(
    private alojamientosService: AlojamientosService,
    private router: Router,
    private dialog: MatDialog
  ) {
    this.getAlojamientosActivos();
  }


  getAlojamientosActivos() {
    this.alojamientosService.obtenerAlojamientosActivos().subscribe({
      next: (data: any[]) => {
        console.log(data);
        this.destinations = data.map((item: any) => {
          const imgs = item.imagenes?.map((img: any) => ({
            ...img,
            url: img.url.startsWith('/api') ? `http://localhost:8080${img.url}` : img.url
          })) || [];

          return {
            id: item.id,
            nombre: item.nombre,
            direccion: item.direccion,
            precio: item.precio,
            descripcion: item.descripcion,
            imagenes: imgs,
            rating: item.rating || 5, // Default rating if missing
            favorite: false
          };
        });

        // Group properties by Country or prominent City
        this.groupDestinations(this.destinations);
      },
      error: (error) => {
        console.error('Error al obtener alojamientos activos:', error);
      }
    });
  }

  groupDestinations(destinationsToGroup: Destination[]) {
    this.groupedDestinations = {};

    if (!destinationsToGroup || destinationsToGroup.length === 0) return;

    destinationsToGroup.forEach(dest => {
      // Intentar extraer el país (ej: "Medellín, Colombia" -> "Colombia")
      const parts = dest.direccion.split(',');
      let groupName = 'Otros Destinos';

      if (parts.length > 1) {
        groupName = `Alojamientos en ${parts[parts.length - 1].trim()}`;
      } else if (parts.length === 1 && parts[0].trim() !== '') {
        groupName = `Alojamientos en ${parts[0].trim()}`;
      }

      if (!this.groupedDestinations[groupName]) {
        this.groupedDestinations[groupName] = [];
      }
      this.groupedDestinations[groupName].push(dest);
    });
  }

  openCheckInDialog() {
    const dialogRef = this.dialog.open(DatePickerDialog, {
      data: { selectedDate: this.checkInDate, title: 'Selecciona fecha de llegada' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.checkInDate = result;
      }
    });
  }

  openCheckOutDialog() {
    const dialogRef = this.dialog.open(DatePickerDialog, {
      data: { selectedDate: this.checkOutDate, title: 'Selecciona fecha de salida' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.checkOutDate = result;
      }
    });
  }

  formatDate(date: Date | null, placeholder: string = 'Agrega fecha'): string {
    if (!date) return placeholder;
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




  // Extraer las llaves para iterar en el HTML más fácil
  get groupKeys(): string[] {
    return Object.keys(this.groupedDestinations);
  }

}
