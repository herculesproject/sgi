import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';
import { TipoConvocatoriaReunionService } from './tipo-convocatoria-reunion.service';


describe('TipoConvocatoriaReunionService', () => {
  let service: TipoConvocatoriaReunionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(TipoConvocatoriaReunionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
