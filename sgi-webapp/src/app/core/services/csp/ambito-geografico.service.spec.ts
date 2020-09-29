import { TestBed } from '@angular/core/testing';

import { AmbitoGeograficoService } from './ambito-geografico.service';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('AmbitoGeograficoService', () => {
  let service: AmbitoGeograficoService;

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
    service = TestBed.inject(AmbitoGeograficoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
