import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ModeloUnidadService } from './modelo-unidad.service';

describe('ModeloTipoUnidadService', () => {
  let service: ModeloUnidadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ],
      providers: [
      ],
    });
    service = TestBed.inject(ModeloUnidadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
