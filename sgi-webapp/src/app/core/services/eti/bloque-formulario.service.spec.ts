import { TestBed } from '@angular/core/testing';

import { BloqueFormularioService } from './bloque-formulario.service';
import { HttpClientModule } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('BloqueFormularioService', () => {
  let service: BloqueFormularioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(BloqueFormularioService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
