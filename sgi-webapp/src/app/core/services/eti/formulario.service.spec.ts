import { TestBed } from '@angular/core/testing';

import { FormularioService } from './formulario.service';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { LayoutService } from '@core/services/layout.service';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';

describe('FormularioService', () => {
  let service: FormularioService;

  beforeEach(() => {

    // Mock logger
    const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(NGXLogger.name, TestUtils.getOwnMethodNames(NGXLogger.prototype));

    // Mock FormulariosDinamicosService
    const tipoFungibleServiceSpy = jasmine.createSpyObj(FormularioService.name,
      TestUtils.getMethodNames(FormularioService.prototype));

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
        { provide: FormularioService, useValue: tipoFungibleServiceSpy },
        LayoutService,
      ]
    });
    service = TestBed.inject(FormularioService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});