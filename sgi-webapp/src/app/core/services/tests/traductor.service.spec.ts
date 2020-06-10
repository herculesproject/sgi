import { TestBed } from '@angular/core/testing';

import { LayoutService } from '../layout.service';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/test-utils';
import { TraductorService } from '../traductor.service';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

describe('LayoutService', () => {
  let service: TraductorService;

  beforeEach(() => {
    // Mock logger
    const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(
      NGXLogger.name,
      TestUtils.getOwnMethodNames(NGXLogger.prototype)
    );

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
        {
          provide: NGXLogger,
          useValue: loggerSpy
        },
        LayoutService
      ]
    });
    service = TestBed.inject(TraductorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
