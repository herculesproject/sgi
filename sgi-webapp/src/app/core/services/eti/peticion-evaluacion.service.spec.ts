import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SgiAuthModule, SgiAuthService } from '@herculesproject/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { PeticionEvaluacionService } from './peticion-evaluacion.service';

describe('PeticionEvaluacionService', () => {
  let service: PeticionEvaluacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule,
        SgiAuthModule
      ],
      providers: [
        SgiAuthService
      ],
    });
    service = TestBed.inject(PeticionEvaluacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
