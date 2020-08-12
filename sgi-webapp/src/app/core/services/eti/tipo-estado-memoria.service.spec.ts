import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';

import { TipoEstadoMemoriaService } from './tipo-estado-memoria.service';

describe('TipoEstadoMemoriaService', () => {
  let service: TipoEstadoMemoriaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(TipoEstadoMemoriaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
