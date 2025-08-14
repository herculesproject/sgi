import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { TipoComentarioService } from './tipo-comentario.service';

describe('TipoComentarioService', () => {
  let service: TipoComentarioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        FormsModule,
        LoggerTestingModule,
        SgiAuthModule
      ]
    });
    service = TestBed.inject(TipoComentarioService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
