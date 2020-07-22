import { TestBed } from '@angular/core/testing';

import { SupervisionService } from './supervision.service';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';
import { HttpClientModule, } from '@angular/common/http';
import { LayoutService } from '../layout.service';
import { RouterTestingModule } from '@angular/router/testing';

describe('SupervisionService', () => {
  let service: SupervisionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        TestUtils.getIdiomas(),
        RouterTestingModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        LayoutService,
      ],
    });
    service = TestBed.inject(SupervisionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
