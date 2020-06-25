import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TipoFungibleAgregarActualizarComponent } from './tipo-fungible-agregar-actualizar.component';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { AppRoutingModule } from 'src/app/app-routing.module';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

describe('TipoFungibleAgregarActualizarComponent', () => {
  let component: TipoFungibleAgregarActualizarComponent;
  let fixture: ComponentFixture<TipoFungibleAgregarActualizarComponent>;
  const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(
    NGXLogger.name,
    TestUtils.getOwnMethodNames(NGXLogger.prototype)
  );

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TipoFungibleAgregarActualizarComponent],
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
    fixture = TestBed.createComponent(TipoFungibleAgregarActualizarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
