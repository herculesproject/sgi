import { HttpClient } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { NGXLogger } from 'ngx-logger';

import { UnidadMedidaListadoComponent } from './unidad-medida-listado.component';

describe('UnidadMedidaListadoComponent', () => {
  let component: UnidadMedidaListadoComponent;
  let fixture: ComponentFixture<UnidadMedidaListadoComponent>;

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

    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useFactory: (http: HttpClient) => {
              return new TranslateHttpLoader(http);
            },
            deps: [HttpClient],
          },
        }),
        RouterTestingModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: loggerSpy },
        { provide: SnackBarService, useValue: snackBarServiceSpy },
      ],
      declarations: [UnidadMedidaListadoComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UnidadMedidaListadoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
