import { TestBed } from '@angular/core/testing';

import { PeticionEvaluacionService } from './peticion-evaluacion.service';
import { HttpClientModule } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('PeticionEvaluacionService', () => {
  let service: PeticionEvaluacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(PeticionEvaluacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
