import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Anfitrion } from './anfitrion';

describe('Anfitrion', () => {
  let component: Anfitrion;
  let fixture: ComponentFixture<Anfitrion>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Anfitrion]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Anfitrion);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
