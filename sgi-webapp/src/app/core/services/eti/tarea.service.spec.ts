import { TestBed } from '@angular/core/testing';

import { TareaService } from './tarea.service';
import { HttpClientModule } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('TareaService', () => {
  let service: TareaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(TareaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
