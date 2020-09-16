import { TestBed } from '@angular/core/testing';

import { HttpClientModule } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { EquipoTrabajoService } from './equipo-trabajo.service';

describe('EquipoTrabajoService', () => {
  let service: EquipoTrabajoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(EquipoTrabajoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
