import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SgiAuthModule, SgiAuthService } from '@herculesproject/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { PersonaPublicService } from './persona-public.service';

describe('PersonaPublicService', () => {
  let service: PersonaPublicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule,
        SgiAuthModule
      ],
      providers: [
        SgiAuthService
      ],
    });
    service = TestBed.inject(PersonaPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
