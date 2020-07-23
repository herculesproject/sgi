import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';
import { ComiteService } from './comite.service';


describe('ComiteService', () => {
  let service: ComiteService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(ComiteService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
