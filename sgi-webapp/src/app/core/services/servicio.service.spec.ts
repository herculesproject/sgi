import { TestBed } from '@angular/core/testing';

import { ServicioService } from './servicio.service';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { LayoutService } from './layout.service';

describe('ServicioService', () => {
  let service: ServicioService;

  beforeEach(() => {

    // Mock logger
    const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(NGXLogger.name, TestUtils.getOwnMethodNames(NGXLogger.prototype));

    // Mock ServicioService
    const servicioServiceSpy = jasmine.createSpyObj(ServicioService.name,
      TestUtils.getMethodNames(ServicioService.prototype));

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
        { provide: ServicioService, useValue: servicioServiceSpy },
        LayoutService,
      ]
    });

    service = TestBed.inject(ServicioService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
