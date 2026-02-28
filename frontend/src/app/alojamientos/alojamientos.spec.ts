import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Alojamientos } from './alojamientos';

describe('Alojamientos', () => {
  let component: Alojamientos;
  let fixture: ComponentFixture<Alojamientos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Alojamientos]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Alojamientos);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
