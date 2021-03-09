import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { IProyectoSocioPeriodoJustificacion } from '@core/models/csp/proyecto-socio-periodo-justificacion';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { IProyectoSocioPeriodoJustificacionState } from '../../../proyecto-socio/proyecto-socio-formulario/proyecto-socio-periodo-justificacion/proyecto-socio-periodo-justificacion.component';
import { ProyectoSocioPeriodoJustificacionActionService } from '../../proyecto-socio-periodo-justificacion.action.service';
import { ProyectoSocioPeriodoJustificacionDatosGeneralesComponent } from './proyecto-socio-periodo-justificacion-datos-generales.component';

describe('ProyectoSocioPeriodoJustificacionDatosGeneralesComponent', () => {
  let component: ProyectoSocioPeriodoJustificacionDatosGeneralesComponent;
  let fixture: ComponentFixture<ProyectoSocioPeriodoJustificacionDatosGeneralesComponent>;

  const proyectoSocio: IProyectoSocio = {
    empresa: undefined,
    fechaFin: undefined,
    fechaInicio: undefined,
    id: undefined,
    importeConcedido: undefined,
    numInvestigadores: undefined,
    proyecto: undefined,
    rolSocio: undefined
  };

  const periodoJustificacion: IProyectoSocioPeriodoJustificacion = {
    documentacionRecibida: undefined,
    fechaFin: undefined,
    fechaFinPresentacion: undefined,
    fechaInicio: undefined,
    fechaInicioPresentacion: undefined,
    fechaRecepcion: undefined,
    id: undefined,
    numPeriodo: undefined,
    observaciones: undefined,
    proyectoSocio
  };

  const state: IProyectoSocioPeriodoJustificacionState = {
    periodoJustificacion,
    selectedPeriodosJustificacion: [],
    proyectoId: 1,
    proyectoSocio,
    urlProyecto: '',
    urlProyectoSocio: ''
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ProyectoSocioPeriodoJustificacionDatosGeneralesComponent],
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
        ProyectoSocioPeriodoJustificacionActionService,
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    spyOnProperty(history, 'state', 'get').and.returnValue(state);
    fixture = TestBed.createComponent(ProyectoSocioPeriodoJustificacionDatosGeneralesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
