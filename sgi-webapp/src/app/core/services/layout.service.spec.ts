import { TestBed } from '@angular/core/testing';

import { LayoutService } from './layout.service';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { HttpClientModule } from '@angular/common/http';

describe('LayoutService', () => {
  let service: LayoutService;

  beforeEach(() => {
    // Mock logger
    const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(
      NGXLogger.name,
      TestUtils.getOwnMethodNames(NGXLogger.prototype)
    );

    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [{ provide: NGXLogger, useValue: loggerSpy }],
    });
    service = TestBed.inject(LayoutService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
