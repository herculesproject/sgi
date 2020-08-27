import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';

import { TipoComentarioService } from './tipo-comentario.service';

describe('TipoComentarioService', () => {
  let service: TipoComentarioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(TipoComentarioService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
