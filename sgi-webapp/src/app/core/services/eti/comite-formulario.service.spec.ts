import { TestBed } from '@angular/core/testing';

import { ComiteFormularioService } from './comite-formulario.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth/public-api';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('ComiteFormularioService', () => {
  let service: ComiteFormularioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        SgiAuthModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        SgiAuthService
      ],
    });
    service = TestBed.inject(ComiteFormularioService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
