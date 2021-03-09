import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoPeriodoSeguimiento } from '@core/models/csp/proyecto-periodo-seguimiento';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { DateTime } from 'luxon';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { IProyectoPeriodoSeguimientoState } from '../../../proyecto/proyecto-formulario/proyecto-periodo-seguimientos/proyecto-periodo-seguimientos.component';
import { ProyectoPeriodoSeguimientoActionService } from '../../proyecto-periodo-seguimiento.action.service';
import { ProyectoPeriodoSeguimientoDatosGeneralesComponent } from './proyecto-periodo-seguimiento-datos-generales.component';

describe('ProyectoPeriodoSeguimientoDatosGeneralesComponent', () => {
  let component: ProyectoPeriodoSeguimientoDatosGeneralesComponent;
  let fixture: ComponentFixture<ProyectoPeriodoSeguimientoDatosGeneralesComponent>;

  const proyecto: IProyecto = {
    id: 1,
    fechaInicio: DateTime.now(),
    fechaFin: DateTime.now()
  } as IProyecto;

  const proyectoPeriodoSeguimiento: IProyectoPeriodoSeguimiento = {
    id: 1,
    proyecto,
    fechaInicio: DateTime.now(),
    fechaFin: DateTime.now(),
    numPeriodo: 1
  } as IProyectoPeriodoSeguimiento;

  const selectedProyectoPeriodoSeguimientos: IProyectoPeriodoSeguimiento[] = [
    proyectoPeriodoSeguimiento as IProyectoPeriodoSeguimiento]

  const state: IProyectoPeriodoSeguimientoState = {
    proyecto,
    proyectoPeriodoSeguimiento,
    selectedProyectoPeriodoSeguimientos,
    readonly: true
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ProyectoPeriodoSeguimientoDatosGeneralesComponent
      ],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        FlexModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        SharedModule
      ],
      providers: [
        ProyectoPeriodoSeguimientoActionService,
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    spyOnProperty(history, 'state', 'get').and.returnValue(state);

    fixture = TestBed.createComponent(ProyectoPeriodoSeguimientoDatosGeneralesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
