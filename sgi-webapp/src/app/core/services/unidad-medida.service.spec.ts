import { TestBed } from '@angular/core/testing';

import { UnidadMedidaService } from './unidad-medida.service';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { NGXLogger } from 'ngx-logger';
import { LayoutService } from './layout.service';
import TestUtils from '@core/utils/test-utils';

describe('UnidadMedidaService', () => {
  let service: UnidadMedidaService;

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
            deps: [HttpClient],
          },
        }),
      ],
      providers: [
        { provide: NGXLogger, useValue: loggerSpy },
        LayoutService,
      ],
    });
    service = TestBed.inject(UnidadMedidaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
