import {TestBed} from '@angular/core/testing';

import {UnidadMedidaService} from './unidad-medida.service';
import {HttpClientModule} from '@angular/common/http';
import {NGXLogger} from 'ngx-logger';
import {LayoutService} from './layout.service';
import TestUtils from '@core/utils/test-utils';
import {RouterTestingModule} from '@angular/router/testing';

describe('UnidadMedidaService', () => {
  let service: UnidadMedidaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        TestUtils.getIdiomas(),
        RouterTestingModule
      ],
      providers: [
        {provide: NGXLogger, useValue: TestUtils.getLoggerSpy()},
        LayoutService,
      ],
    });
    service = TestBed.inject(UnidadMedidaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
