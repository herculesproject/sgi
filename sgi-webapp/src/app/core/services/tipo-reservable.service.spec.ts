import { TestBed } from '@angular/core/testing';

import { TipoReservableService } from './tipo-reservable.service';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { NGXLogger } from 'ngx-logger';
import { LayoutService } from './layout.service';
import TestUtils from '@core/utils/test-utils';

describe('TipoReservableService', () => {
  let service: TipoReservableService;

  beforeEach(() => {

    // Mock logger
    const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(NGXLogger.name, TestUtils.getOwnMethodNames(NGXLogger.prototype));

    // Mock TipoReservableService
    const tipoReservableServiceSpy = jasmine.createSpyObj(TipoReservableService.name,
      TestUtils.getMethodNames(TipoReservableService.prototype));

    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useFactory: (http: HttpClient) => {
              return new TranslateHttpLoader(http);
            },
            deps: [HttpClient]
          },
        }),
      ],
      providers: [
        { provide: NGXLogger, useValue: loggerSpy },
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
