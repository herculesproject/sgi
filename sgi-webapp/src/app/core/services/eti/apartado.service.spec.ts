import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';

import { ApartadoService } from './apartado.service';

describe('ApartadoService', () => {
  let service: ApartadoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        SgiAuthModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        SgiAuthService
      ],
    });
    service = TestBed.inject(ApartadoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
