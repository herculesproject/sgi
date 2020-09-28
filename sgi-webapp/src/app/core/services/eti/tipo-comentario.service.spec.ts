import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { SgiAuthModule } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';

import { TipoComentarioService } from './tipo-comentario.service';

describe('TipoComentarioService', () => {
  let service: TipoComentarioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        FormsModule,
        SgiAuthModule
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
