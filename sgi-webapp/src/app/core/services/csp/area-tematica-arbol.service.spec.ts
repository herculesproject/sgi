import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';

import { AreaTematicaArbolService } from './area-tematica-arbol.service';

describe('AreaTematicaArbolService', () => {
  let service: AreaTematicaArbolService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(AreaTematicaArbolService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
