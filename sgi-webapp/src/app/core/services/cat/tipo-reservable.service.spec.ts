import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';

import { LayoutService } from '../layout.service';
import { TipoReservableService } from './tipo-reservable.service';

describe('TipoReservableService', () => {
  let service: TipoReservableService;

  beforeEach(() => {

    // Mock TipoReservableService
    const tipoReservableServiceSpy = jasmine.createSpyObj(TipoReservableService.name,
      TestUtils.getMethodNames(TipoReservableService.prototype));

    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        TestUtils.getIdiomas()
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: TipoReservableService, useValue: tipoReservableServiceSpy },
        LayoutService,
      ]
    });
    service = TestBed.inject(TipoReservableService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
