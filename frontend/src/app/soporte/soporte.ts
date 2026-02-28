import { Component } from '@angular/core';
import { AccordionModule } from 'primeng/accordion';
import { ButtonModule } from 'primeng/button';
import { CommonModule } from '@angular/common';
import { Menu } from "../components/menu/menu";

@Component({
  selector: 'app-soporte',
  imports: [
    CommonModule,
    AccordionModule,
    ButtonModule,
    Menu
],
  templateUrl: './soporte.html',
  styleUrl: './soporte.scss',
})
export class Soporte {

}
