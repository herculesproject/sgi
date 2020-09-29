import { TestBed } from '@angular/core/testing';

import { UnidadGestionService } from './unidad-gestion.service';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('UnidadGestionService', () => {
  let service: UnidadGestionService;

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
    service = TestBed.inject(UnidadGestionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
