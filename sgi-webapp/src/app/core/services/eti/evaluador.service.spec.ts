import { TestBed } from '@angular/core/testing';

import { EvaluadorService } from './evaluador.service';
import { HttpClientModule } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('EvaluadorService', () => {
  let service: EvaluadorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(EvaluadorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
