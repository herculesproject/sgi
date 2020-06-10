import { TestBed } from '@angular/core/testing';

import { LayoutService } from '../layout.service';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/test-utils';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { SERVICES_IMPORTS } from '../exports/services-export';

describe('LayoutService', () => {
  let service: LayoutService;

  beforeEach(() => {

    // Mock logger
    const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(NGXLogger.name, TestUtils.getOwnMethodNames(NGXLogger.prototype));

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
          }
        }),
      ],
      providers: [
        { provide: NGXLogger, useValue: loggerSpy },
        LayoutService,
        SERVICES_IMPORTS,
      ]
    });
    service = TestBed.inject(LayoutService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
