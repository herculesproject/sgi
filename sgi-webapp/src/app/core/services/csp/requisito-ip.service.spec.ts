import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { RequisitoIPService } from './requisito-ip.service';

describe('RequisitoIPService', () => {
  let service: RequisitoIPService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(RequisitoIPService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});