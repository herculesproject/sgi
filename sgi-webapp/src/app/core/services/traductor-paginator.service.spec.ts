import { TestBed } from '@angular/core/testing';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';

import { TraductorPaginatorService } from './traductor-paginator.service';

describe('TraductorPaginatorService', () => {
  let service: TraductorPaginatorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TestUtils.getIdiomas()],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ]
    });
    service = TestBed.inject(TraductorPaginatorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
