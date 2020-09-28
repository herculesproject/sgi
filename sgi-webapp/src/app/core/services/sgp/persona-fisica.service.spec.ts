import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import TestUtils from '@core/utils/test-utils';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';

import { PersonaFisicaService } from './persona-fisica.service';

describe('PersonaFisicaService', () => {
  let service: PersonaFisicaService;

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
    service = TestBed.inject(PersonaFisicaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
