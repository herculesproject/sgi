import { TestBed } from '@angular/core/testing';
import { FormacionEspecificaService } from './formacion-especifica.service';
import { HttpClientModule } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('FormacionEspecificaService', () => {
  let service: FormacionEspecificaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(FormacionEspecificaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
