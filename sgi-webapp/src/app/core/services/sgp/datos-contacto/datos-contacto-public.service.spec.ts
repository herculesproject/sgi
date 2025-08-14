import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SgiAuthModule, SgiAuthService } from '@herculesproject/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { DatosContactoPublicService } from './datos-contacto-public.service';

describe('DatosContactoPublicService', () => {
  let service: DatosContactoPublicService;

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
    service = TestBed.inject(DatosContactoPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
