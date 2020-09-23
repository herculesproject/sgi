import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';

import { DictamenService } from './dictamen.service';

describe('DictamenService', () => {
  let service: DictamenService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(DictamenService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
