import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { HeaderComponent } from '@block/header/header.component';
import { NavbarComponent } from '@block/navbar/navbar.component';
import { NGXLogger } from 'ngx-logger';

import { AppComponent } from './app.component';
import { SgiAuthService } from '@sgi/framework/auth';

describe('AppComponent', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
        MaterialDesignModule,
        BrowserAnimationsModule,
        TestUtils.getIdiomas()
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        SgiAuthService
      ],
      declarations: [
        AppComponent,
        HeaderComponent,
        NavbarComponent
      ]
    }).compileComponents();
  }));

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
