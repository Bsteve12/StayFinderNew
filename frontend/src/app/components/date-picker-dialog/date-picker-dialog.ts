import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { Component, inject, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { provideNativeDateAdapter, MAT_DATE_FORMATS, MAT_NATIVE_DATE_FORMATS } from '@angular/material/core';


// Dialog Component para el DatePicker
@Component({
  selector: 'date-picker-dialog',
  imports: [
    CommonModule,
    MatDatepickerModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule,
  ],
  providers: [
    provideNativeDateAdapter(),
    { provide: MAT_DATE_FORMATS, useValue: MAT_NATIVE_DATE_FORMATS },
  ],
  templateUrl: './date-picker-dialog.html',
  styleUrl: './date-picker-dialog.scss',
})
export class DatePickerDialog {
  dialogRef = inject<MatDialogRef<DatePickerDialog>>(MatDialogRef<DatePickerDialog>);
  data = inject(MAT_DIALOG_DATA);
  readonly date = new FormControl(new Date());

  constructor() {
    if (this.data.selectedDate) {
      this.date.setValue(this.data.selectedDate);
    }
  }

  onCancel() {
    this.dialogRef.close();
  }

  onConfirm() {
    this.dialogRef.close(this.date.value);
  }
}
