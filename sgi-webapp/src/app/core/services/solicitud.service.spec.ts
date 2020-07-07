import { TestBed } from '@angular/core/testing';

import { SolicitudService } from './solicitud.service';
import { HttpClientModule } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import { LayoutService } from './layout.service';
import TestUtils from '@core/utils/test-utils';
import { RouterTestingModule } from '@angular/router/testing';

describe('SolicitudService', () => {
  let service: SolicitudService;

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
    service = TestBed.inject(SolicitudService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
