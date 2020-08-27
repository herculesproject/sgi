import { TestBed } from '@angular/core/testing';

import { ApartadoFormularioService } from './apartado-formulario.service';
import { HttpClientModule } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('ApartadoFormularioService', () => {
  let service: ApartadoFormularioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(ApartadoFormularioService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
