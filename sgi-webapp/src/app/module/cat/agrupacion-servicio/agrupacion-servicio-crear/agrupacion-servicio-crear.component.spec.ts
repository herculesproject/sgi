import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
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
import { AgrupacionServicioCrearComponent } from './agrupacion-servicio-crear.component';

describe('AgrupacionServicioCrearComponent', () => {
  let component: AgrupacionServicioCrearComponent;
  let fixture: ComponentFixture<AgrupacionServicioCrearComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        TestUtils.getIdiomas(),
        ReactiveFormsModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
      ],
      declarations: [
        AgrupacionServicioCrearComponent,
        AgrupacionServicioDatosGeneralesComponent,
        AgrupacionServicioGestorComponent
      ]
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
