import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';

import { ModeloTipoDocumentoService } from './modelo-tipo-documento.service';

describe('ModeloTipoDocumentoService', () => {
  let service: ModeloTipoDocumentoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(ModeloTipoDocumentoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});