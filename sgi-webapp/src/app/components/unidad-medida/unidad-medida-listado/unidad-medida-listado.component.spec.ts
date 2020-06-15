import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UnidadMedidaListadoComponent } from './unidad-medida-listado.component';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialDesignModule } from '@material/material-design.module';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

describe('UnidadMedidaListadoComponent', () => {
  let component: UnidadMedidaListadoComponent;
  let fixture: ComponentFixture<UnidadMedidaListadoComponent>;

  beforeEach(async(() => {

    // Mock logger
    const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(NGXLogger.name, TestUtils.getOwnMethodNames(NGXLogger.prototype));

    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientModule,
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
        { provide: NGXLogger, useValue: loggerSpy }
      ],
      declarations: [UnidadMedidaListadoComponent]
    })
      .compileComponents();
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
