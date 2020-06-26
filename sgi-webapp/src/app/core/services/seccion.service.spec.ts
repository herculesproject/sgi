import { TestBed } from '@angular/core/testing';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { LayoutService } from './layout.service';

import { SeccionService } from './seccion.service';

describe('SeccionService', () => {
  let service: SeccionService;

  beforeEach(() => {

    // Mock logger
    const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(NGXLogger.name, TestUtils.getOwnMethodNames(NGXLogger.prototype));

    // Mock SeccionService
    const SeccionServiceSpy = jasmine.createSpyObj(SeccionService.name,
      TestUtils.getMethodNames(SeccionService.prototype));

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
