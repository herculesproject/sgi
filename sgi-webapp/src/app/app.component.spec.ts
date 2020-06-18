import { TestBed, async } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { NGXLogger } from 'ngx-logger';

import TestUtils from '@core/utils/test-utils';
import { AppComponent } from './app.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HeaderComponent } from '@shared/componentes-layout/header/header.component';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { MaterialDesignModule } from '@material/material-design.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MenuPrincipalComponent } from '@shared/componentes-layout/menu-principal/menu-principal.component';
import { MenuSecundarioComponent } from '@shared/componentes-layout/menu-secundario/menu-secundario.component';

describe('AppComponent', () => {
  beforeEach(async(() => {
    // Mock logger
    const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(NGXLogger.name, TestUtils.getOwnMethodNames(NGXLogger.prototype));

    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
        MaterialDesignModule.forRoot(),
        BrowserAnimationsModule,
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useFactory: (http: HttpClient) => {
              return new TranslateHttpLoader(http);
            },
            deps: [HttpClient],
          },
        }),
      ],
      providers: [
        { provide: NGXLogger, useValue: loggerSpy }
      ],
      declarations: [
        AppComponent,
        HeaderComponent,
        MenuPrincipalComponent,
        MenuSecundarioComponent
      ]
    }).compileComponents();
  }));

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
