import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConvertirseAnfitrion } from './convertirse-anfitrion';

describe('ConvertirseAnfitrion', () => {
  let component: ConvertirseAnfitrion;
  let fixture: ComponentFixture<ConvertirseAnfitrion>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConvertirseAnfitrion]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConvertirseAnfitrion);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
