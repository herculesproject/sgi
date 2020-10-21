import { TestBed } from '@angular/core/testing';

import { ComiteFormularioService } from './comite-formulario.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('ComiteFormularioService', () => {
  let service: ComiteFormularioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() }
      ],
    });
    service = TestBed.inject(ComiteFormularioService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
