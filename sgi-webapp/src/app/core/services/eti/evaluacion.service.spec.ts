import { TestBed } from '@angular/core/testing';

import { EvaluacionService } from './evaluacion.service';
import { HttpClientModule } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('EvaluacionService', () => {
  let service: EvaluacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(EvaluacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
