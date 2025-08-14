import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { EvaluacionService } from './evaluacion.service';

describe('EvaluacionService', () => {
  let service: EvaluacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule,
        SgiAuthModule
      ],
    });
    service = TestBed.inject(EvaluacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
