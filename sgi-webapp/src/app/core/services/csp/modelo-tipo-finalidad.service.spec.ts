import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';

import { ModeloTipoFinalidadService } from './modelo-tipo-finalidad.service';

describe('ModeloTipoFinalidadService', () => {
  let service: ModeloTipoFinalidadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(ModeloTipoFinalidadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
