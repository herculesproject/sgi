import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {SolicitudActualizarComponent} from './solicitud-actualizar.component';
import {NGXLogger} from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import {AppRoutingModule} from 'src/app/app-routing.module';
import {CommonModule} from '@angular/common';
import {TranslateModule} from '@ngx-translate/core';
import {SnackBarService} from '@core/services/snack-bar.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {MaterialDesignModule} from '@material/material-design.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {FlexLayoutModule} from '@angular/flex-layout';
import {MenuSecundarioComponent} from '@shared/componentes-layout/menu-secundario/menu-secundario.component';
import {ReactiveFormsModule} from '@angular/forms';
import {SolicitudListadoComponent} from '../solicitud-listado/solicitud-listado.component';
import {RouterTestingModule} from '@angular/router/testing';

describe('SolicitudActualizarComponent', () => {
  let component: SolicitudActualizarComponent;
  let fixture: ComponentFixture<SolicitudActualizarComponent>;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        SolicitudActualizarComponent,
        MenuSecundarioComponent,
        SolicitudListadoComponent
      ],
      providers: [
        {provide: NGXLogger, useValue: TestUtils.getLoggerSpy()},
        {provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy()},
      ],
      imports: [
        TestUtils.getIdiomas(),
        AppRoutingModule,
        HttpClientTestingModule,
        CommonModule,
        TranslateModule,
        MaterialDesignModule.forRoot(),
        BrowserAnimationsModule,
        FlexLayoutModule,
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([]),
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudActualizarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
