import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PagarReserva } from './pagar-reserva';

describe('PagarReserva', () => {
  let component: PagarReserva;
  let fixture: ComponentFixture<PagarReserva>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PagarReserva]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PagarReserva);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
