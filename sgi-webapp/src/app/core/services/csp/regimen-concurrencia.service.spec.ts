import { TestBed } from '@angular/core/testing';

import { RegimenConcurrenciaService } from './regimen-concurrencia.service';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('RegimenConcurrenciaService', () => {
  let service: RegimenConcurrenciaService;

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
    service = TestBed.inject(RegimenConcurrenciaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
