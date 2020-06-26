import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AgrupacionServicioActualizarComponent } from './agrupacion-servicio-actualizar.component';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { RouterTestingModule } from '@angular/router/testing';
import { MaterialDesignModule } from '@material/material-design.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormBuilder } from '@angular/forms';

describe('AgrupacionServicioActualizarComponent', () => {
  let component: AgrupacionServicioActualizarComponent;
  let fixture: ComponentFixture<AgrupacionServicioActualizarComponent>;

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
      declarations: [AgrupacionServicioActualizarComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AgrupacionServicioActualizarComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
