import { TestBed } from '@angular/core/testing';

import { DialogService } from './dialog.service';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { MatDialogModule } from '@angular/material/dialog';
import { RouterTestingModule } from '@angular/router/testing';

describe('DialogService', () => {
  let service: DialogService;

  beforeEach(() => {
    // Mock logger
    const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(
      NGXLogger.name,
      TestUtils.getOwnMethodNames(NGXLogger.prototype)
    );

    TestBed.configureTestingModule({
      imports: [MaterialDesignModule, MatDialogModule, RouterTestingModule],
      providers: [{ provide: NGXLogger, useValue: loggerSpy }, DialogService],
    });
    service = TestBed.inject(DialogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
