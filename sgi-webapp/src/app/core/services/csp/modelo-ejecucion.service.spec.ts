import { TestBed } from '@angular/core/testing';

import { ModeloEjecucionService } from './modelo-ejecucion.service';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('ModeloEjecucionService', () => {
  let service: ModeloEjecucionService;

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
    service = TestBed.inject(ModeloEjecucionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
