import { TestBed } from '@angular/core/testing';

import { TipoTareaService } from './tipo-tarea.service';
import { HttpClientModule } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('TipoTareaService', () => {
  let service: TipoTareaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(TipoTareaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
