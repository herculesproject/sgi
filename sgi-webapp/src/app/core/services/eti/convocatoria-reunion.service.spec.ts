import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';

import { ConvocatoriaReunionService } from './convocatoria-reunion.service';

describe('ConvocatoriaReunionService', () => {
  let service: ConvocatoriaReunionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(ConvocatoriaReunionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
