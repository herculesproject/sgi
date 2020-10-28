import { TestBed } from '@angular/core/testing';

import { TipoAmbitoGeograficoService } from './tipo-ambito-geografico.service';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('TipoAmbitoGeograficoService', () => {
  let service: TipoAmbitoGeograficoService;

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
    service = TestBed.inject(TipoAmbitoGeograficoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
