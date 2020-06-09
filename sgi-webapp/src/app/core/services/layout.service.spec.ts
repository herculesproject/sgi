import { TestBed } from '@angular/core/testing';

import { LayoutService } from './layout.service';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/test-utils';

describe('LayoutService', () => {
  let service: LayoutService;

  beforeEach(() => {

    // Mock logger
    const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(NGXLogger.name, TestUtils.getOwnMethodNames(NGXLogger.prototype));

    TestBed.configureTestingModule({
      imports: [],
      providers: [
        { provide: NGXLogger, useValue: loggerSpy },
        LayoutService
      ]
    });
    service = TestBed.inject(LayoutService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
