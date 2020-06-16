import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UnidadMedidaAgregarActualizarComponent } from './unidad-medida-agregar-actualizar.component';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { AppRoutingModule } from 'src/app/app-routing.module';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { SnackBarService } from '@core/services/snack-bar.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('UnidadMedidaAgregarActualizarComponent', () => {
  let component: UnidadMedidaAgregarActualizarComponent;
  let fixture: ComponentFixture<UnidadMedidaAgregarActualizarComponent>;
  const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(
    NGXLogger.name,
    TestUtils.getOwnMethodNames(NGXLogger.prototype)
  );

  // Mock SnackBarService
  const snackBarServiceSpy: jasmine.SpyObj<SnackBarService> =
    jasmine.createSpyObj(SnackBarService.name, TestUtils.getOwnMethodNames(SnackBarService.prototype));


  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [UnidadMedidaAgregarActualizarComponent],
      providers: [
        { provide: NGXLogger, useValue: loggerSpy },
        { provide: SnackBarService, useValue: snackBarServiceSpy }
      ],
      imports: [
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useFactory: (http: HttpClient) => {
              return new TranslateHttpLoader(http);
            },
            deps: [HttpClient]
          }
        }),
        AppRoutingModule,
        HttpClientTestingModule,
        CommonModule,
        TranslateModule,
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UnidadMedidaAgregarActualizarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
