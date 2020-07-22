import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';

import { LayoutService } from '../layout.service';
import { TipoFungibleService } from './tipo-fungible.service';

describe('TipoFungibleService', () => {
  let service: TipoFungibleService;

  beforeEach(() => {
    // Mock TipoFungibleService
    const tipoFungibleServiceSpy = jasmine.createSpyObj(TipoFungibleService.name,
      TestUtils.getMethodNames(TipoFungibleService.prototype));

    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        TestUtils.getIdiomas()
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: TipoFungibleService, useValue: tipoFungibleServiceSpy },
        LayoutService,
      ]
    });
    service = TestBed.inject(TipoFungibleService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
