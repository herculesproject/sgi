import { TestBed } from '@angular/core/testing';

import { ServicioService } from './servicio.service';
import { HttpClientModule } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import { LayoutService } from './layout.service';
import TestUtils from '@core/utils/test-utils';
import { RouterTestingModule } from '@angular/router/testing';

describe('ServicioService', () => {
  let service: ServicioService;

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
    service = TestBed.inject(ServicioService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
