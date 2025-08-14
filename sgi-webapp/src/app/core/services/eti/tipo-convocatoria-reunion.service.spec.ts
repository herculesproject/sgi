import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { TipoConvocatoriaReunionService } from './tipo-convocatoria-reunion.service';


describe('TipoConvocatoriaReunionService', () => {
  let service: TipoConvocatoriaReunionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule,
        SgiAuthModule
      ]
    });
    service = TestBed.inject(TipoConvocatoriaReunionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
