import { TestBed } from '@angular/core/testing';

import { TipoActividadService } from './tipo-actividad.service';
import { HttpClientModule } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('TipoActividadService', () => {
  let service: TipoActividadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ]
    });
    service = TestBed.inject(TipoActividadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
