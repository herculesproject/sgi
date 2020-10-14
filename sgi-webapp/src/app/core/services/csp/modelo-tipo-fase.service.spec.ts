import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';

import { ModeloTipoFaseService } from './modelo-tipo-fase.service';

describe('ModeloTipoFaseService', () => {
  let service: ModeloTipoFaseService;

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
    service = TestBed.inject(ModeloTipoFaseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

