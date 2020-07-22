import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';

import { LayoutService } from '../layout.service';
import { SeccionService } from './seccion.service';

describe('SeccionService', () => {
  let service: SeccionService;

  beforeEach(() => {
    // Mock SeccionService
    const SeccionServiceSpy = jasmine.createSpyObj(SeccionService.name,
      TestUtils.getMethodNames(SeccionService.prototype));

    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        TestUtils.getIdiomas()
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: SeccionService, useValue: SeccionServiceSpy },
        LayoutService,
      ]
    });

    service = TestBed.inject(SeccionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
