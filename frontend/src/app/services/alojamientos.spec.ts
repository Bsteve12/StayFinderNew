import { TestBed } from '@angular/core/testing';
import { AlojamientosService } from './alojamientos';

describe('AlojamientosService', () => {
  let service: AlojamientosService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AlojamientosService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
