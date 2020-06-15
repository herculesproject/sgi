import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UnidadMedidaAgregarActualizarComponent } from './unidad-medida-agregar-actualizar.component';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { AppRoutingModule } from 'src/app/app-routing.module';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TraductorService } from '@core/services/traductor.service';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

describe('UnidadMedidaAgregarActualizarComponent', () => {
  let component: UnidadMedidaAgregarActualizarComponent;
  let fixture: ComponentFixture<UnidadMedidaAgregarActualizarComponent>;
  const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(
    NGXLogger.name,
    TestUtils.getOwnMethodNames(NGXLogger.prototype)
  );

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [UnidadMedidaAgregarActualizarComponent],
      providers: [
        { provide: NGXLogger, useValue: loggerSpy }],
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
        HttpClientModule,
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
