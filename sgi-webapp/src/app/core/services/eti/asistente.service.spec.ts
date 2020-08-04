import { TestBed } from '@angular/core/testing';

import { AsistenteService } from './asistente.service';
import { HttpClientModule } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('AsistenteService', () => {
  let service: AsistenteService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(AsistenteService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
