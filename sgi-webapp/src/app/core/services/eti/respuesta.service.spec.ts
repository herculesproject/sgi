import { TestBed } from '@angular/core/testing';

import { RespuestaService } from './respuesta.service';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { LayoutService } from '../layout.service';

describe('RespuestaService', () => {
  let service: RespuestaService;

  beforeEach(() => {

    // Mock logger
    const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(NGXLogger.name, TestUtils.getOwnMethodNames(NGXLogger.prototype));

    // Mock FormulariosDinamicosService
    const tipoFungibleServiceSpy = jasmine.createSpyObj(RespuestaService.name,
      TestUtils.getMethodNames(RespuestaService.prototype));

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
        { provide: RespuestaService, useValue: tipoFungibleServiceSpy },
        LayoutService,
      ]
    });
    service = TestBed.inject(RespuestaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});