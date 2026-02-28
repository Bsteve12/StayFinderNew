import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DatePickerDialog } from './date-picker-dialog';

describe('DatePickerDialog', () => {
  let component: DatePickerDialog;
  let fixture: ComponentFixture<DatePickerDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DatePickerDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DatePickerDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
