import { TestBed } from '@angular/core/testing';
import { ProgramaService } from './programa.service';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';



describe('ProgramaService', () => {
  let service: ProgramaService;

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
    service = TestBed.inject(ProgramaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
