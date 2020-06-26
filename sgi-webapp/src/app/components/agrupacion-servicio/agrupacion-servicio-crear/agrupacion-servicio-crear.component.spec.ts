import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AgrupacionServicioCrearComponent } from './agrupacion-servicio-crear.component';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { SnackBarService } from '@core/services/snack-bar.service';
import { RouterTestingModule } from '@angular/router/testing';
import { MaterialDesignModule } from '@material/material-design.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { FormBuilder } from '@angular/forms';

describe('AgrupacionServicioCrearComponent', () => {
  let component: AgrupacionServicioCrearComponent;
  let fixture: ComponentFixture<AgrupacionServicioCrearComponent>;

  beforeEach(async(() => {
    // Mock logger
    const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(
      NGXLogger.name,
      TestUtils.getOwnMethodNames(NGXLogger.prototype)
    );

    // Mock SnackBarService
    const snackBarServiceSpy: jasmine.SpyObj<SnackBarService> = jasmine.createSpyObj(
      SnackBarService.name,
      TestUtils.getOwnMethodNames(SnackBarService.prototype)
    );

    // Mock formBuilder
    const formBuilderSpy: jasmine.SpyObj<FormBuilder> = jasmine.createSpyObj(FormBuilder.name,
      TestUtils.getOwnMethodNames(FormBuilder.prototype));

    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useFactory: (http: HttpClient) => {
              return new TranslateHttpLoader(http);
            },
            deps: [HttpClient]
          }
        }),
      ],
      providers: [
        { provide: NGXLogger, useValue: loggerSpy },
        { provide: SnackBarService, useValue: snackBarServiceSpy },
        { provide: FormBuilder, useValue: formBuilderSpy }
      ],
      declarations: [AgrupacionServicioCrearComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AgrupacionServicioCrearComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
