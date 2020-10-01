import { TestBed } from '@angular/core/testing';

import { ConflictoInteresService } from './conflicto-interes.service';
import { HttpClientModule } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('ConflictoInteresService', () => {
  let service: ConflictoInteresService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(ConflictoInteresService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
