import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';

import { DocumentacionMemoriaService } from './documentacion-memoria.service';

describe('DocumentacionMemoriaService', () => {
  let service: DocumentacionMemoriaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() }
      ],
    });
    service = TestBed.inject(DocumentacionMemoriaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
