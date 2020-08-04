import { TestBed } from '@angular/core/testing';
import { UsuarioInfoService } from './usuario-info.service';
import { HttpClientModule } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';


describe('UsuarioInfoService', () => {
  let service: UsuarioInfoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    });
    service = TestBed.inject(UsuarioInfoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
