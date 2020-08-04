import { TestBed } from '@angular/core/testing';

import { EstadoActaService } from './estado-acta.service';
import { HttpClientModule } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('EstadoActaService', () => {
  let service: EstadoActaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(EstadoActaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
