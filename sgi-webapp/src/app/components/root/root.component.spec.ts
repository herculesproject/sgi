import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RootComponent } from './root.component';
import { LoggerModule, NGXLogger } from 'ngx-logger';
import { environment } from '@env';
import TestUtils from '@core/test-utils';

describe('RootComponent', () => {
  let component: RootComponent;
  let fixture: ComponentFixture<RootComponent>;
  const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(NGXLogger.name, TestUtils.getOwnMethodNames(NGXLogger.prototype));

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RootComponent],
      imports: [
        LoggerModule.forRoot({
          serverLoggingUrl: environment.serverLoggingUrl,
          level: environment.logLevel,
          serverLogLevel: environment.serverLogLevel
        }),
      ],
      providers: [
        { provide: NGXLogger, useValue: loggerSpy },
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RootComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
