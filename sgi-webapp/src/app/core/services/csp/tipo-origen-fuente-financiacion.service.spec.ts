import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';

import { TipoOrigenFuenteFinanciacionService } from './tipo-origen-fuente-financiacion.service';

describe('TipoOrigenFuenteFinanciacionService', () => {
  let service: TipoOrigenFuenteFinanciacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(TipoOrigenFuenteFinanciacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
