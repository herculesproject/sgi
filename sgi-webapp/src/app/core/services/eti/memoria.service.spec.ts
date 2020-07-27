import { TestBed } from '@angular/core/testing';

import { MemoriaService } from './memoria.service';
import { HttpClientModule } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('MemoriaService', () => {
  let service: MemoriaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(MemoriaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
