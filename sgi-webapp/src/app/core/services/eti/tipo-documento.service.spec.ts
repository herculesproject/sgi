import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import TestUtils from '@core/utils/test-utils';
import { SgiAuthModule } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';

import { TipoDocumentoService } from './tipo-documento.service';

describe('TipoDocumentoService', () => {
  let service: TipoDocumentoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        SgiAuthModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(TipoDocumentoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
