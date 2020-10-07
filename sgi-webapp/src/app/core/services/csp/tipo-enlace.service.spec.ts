import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';

import { TipoEnlaceService } from './tipo-enlace.service';

describe('TipoEnlaceService', () => {
  let service: TipoEnlaceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(TipoEnlaceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

