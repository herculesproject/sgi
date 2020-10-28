import { TestBed } from '@angular/core/testing';

import { TipoRegimenConcurrenciaService } from './tipo-regimen-concurrencia.service';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('TipoRegimenConcurrenciaService', () => {
  let service: TipoRegimenConcurrenciaService;

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
    service = TestBed.inject(TipoRegimenConcurrenciaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
