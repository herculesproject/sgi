import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';

import { ConvocatoriaEntidadFinanciadoraService } from './convocatoria-entidad-financiadora.service';

describe('ConvocatoriaEntidadFinanciadoraService', () => {
  let service: ConvocatoriaEntidadFinanciadoraService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(ConvocatoriaEntidadFinanciadoraService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
