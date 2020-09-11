import { TestBed } from '@angular/core/testing';

import { EstadoMemoriaService } from './estado-memoria.service';
import { HttpClientModule } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('EstadoMemoriaService', () => {
  let service: EstadoMemoriaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(EstadoMemoriaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
