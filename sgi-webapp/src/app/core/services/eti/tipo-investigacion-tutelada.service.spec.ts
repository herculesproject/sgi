import { TestBed } from '@angular/core/testing';

import { HttpClientModule } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { TipoInvestigacionTuteladaService } from './tipo-investigacion-tutelada.service';

describe('TipoInvestigacionTuteladaService', () => {
  let service: TipoInvestigacionTuteladaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ]
    });
    service = TestBed.inject(TipoInvestigacionTuteladaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
