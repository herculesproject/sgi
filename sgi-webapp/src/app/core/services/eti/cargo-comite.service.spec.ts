import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';
import { CargoComiteService } from './cargo-comite.service';


describe('CargoComiteService', () => {
  let service: CargoComiteService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(CargoComiteService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
