import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SgiAuthModule, SgiAuthService } from '@herculesproject/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { TipoEstadoActaService } from './tipo-estado-acta.service';

describe('TipoEstadoActaService', () => {
  let service: TipoEstadoActaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule,
        SgiAuthModule
      ],
      providers: [
        SgiAuthService
      ],
    });
    service = TestBed.inject(TipoEstadoActaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
