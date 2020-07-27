import { TestBed } from '@angular/core/testing';

import { TipoEstadoActaService } from './tipo-estado-acta.service';
import { HttpClientModule } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('TipoEstadoActaService', () => {
  let service: TipoEstadoActaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(TipoEstadoActaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
