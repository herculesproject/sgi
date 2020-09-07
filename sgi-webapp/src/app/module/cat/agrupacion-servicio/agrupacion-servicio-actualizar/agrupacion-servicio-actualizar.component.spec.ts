import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { NGXLogger } from 'ngx-logger';

import {
  AgrupacionServicioDatosGeneralesComponent,
} from '../agrupacion-servicio-formulario/agrupacion-servicio-datos-generales/agrupacion-servicio-datos-generales.component';
import {
  AgrupacionServicioGestorComponent,
} from '../agrupacion-servicio-formulario/agrupacion-servicio-gestor/agrupacion-servicio-gestor.component';
import { AgrupacionServicioActualizarComponent } from './agrupacion-servicio-actualizar.component';

describe('AgrupacionServicioActualizarComponent', () => {
  let component: AgrupacionServicioActualizarComponent;
  let fixture: ComponentFixture<AgrupacionServicioActualizarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        TestUtils.getIdiomas(),
        BrowserModule,
        FormsModule,
        ReactiveFormsModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
      ],
      declarations: [
        AgrupacionServicioActualizarComponent,
        AgrupacionServicioDatosGeneralesComponent,
        AgrupacionServicioGestorComponent
      ]
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
