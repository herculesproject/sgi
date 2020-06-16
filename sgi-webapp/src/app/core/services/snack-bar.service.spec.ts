import { TestBed } from '@angular/core/testing';

import { SnackBarService } from './snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';

describe('SnackBarService', () => {
  let service: SnackBarService;

  beforeEach(() => {
    // Mock logger
    const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(NGXLogger.name, TestUtils.getOwnMethodNames(NGXLogger.prototype));

    TestBed.configureTestingModule({
      imports: [MaterialDesignModule],
      providers: [
        { provide: NGXLogger, useValue: loggerSpy },
        SnackBarService
      ]
    });
    service = TestBed.inject(SnackBarService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
