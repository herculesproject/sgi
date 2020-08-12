import { TestBed } from '@angular/core/testing';

import { PersonaFisicaService } from './persona-fisica.service';
import { HttpClientModule } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('PersonaFisicaService', () => {
  let service: PersonaFisicaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(PersonaFisicaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
