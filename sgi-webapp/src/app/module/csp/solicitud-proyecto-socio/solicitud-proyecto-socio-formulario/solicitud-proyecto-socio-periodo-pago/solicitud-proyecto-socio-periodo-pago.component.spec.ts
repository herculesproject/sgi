import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SolicitudProyectoSocioPeriodoPagoComponent } from './solicitud-proyecto-socio-periodo-pago.component';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { SharedModule } from '@shared/shared.module';
import { SolicitudProyectoSocioActionService } from '../../solicitud-proyecto-socio.action.service';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { ISolicitudProyectoSocioState } from '../../../solicitud/solicitud-formulario/solicitud-socios-colaboradores/solicitud-socios-colaboradores.component';

describe('SolicitudProyectoSocioPeriodoPagoComponent', () => {
  let component: SolicitudProyectoSocioPeriodoPagoComponent;
  let fixture: ComponentFixture<SolicitudProyectoSocioPeriodoPagoComponent>;

  const solicitudProyectoSocio: ISolicitudProyectoSocio = {
    empresa: undefined,
    id: undefined,
    importeSolicitado: undefined,
    mesFin: undefined,
    mesInicio: undefined,
    numInvestigadores: undefined,
    rolSocio: undefined,
    solicitudProyectoDatos: undefined
  };

  const state: ISolicitudProyectoSocioState = {
    solicitudId: 1,
    solicitudProyectoSocio,
    selectedSolicitudProyectoSocios: []
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        SolicitudProyectoSocioPeriodoPagoComponent
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
        SolicitudProyectoSocioActionService,
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    history.pushState(state.solicitudProyectoSocio, 'solicitudProyectoSocio');
    history.pushState(state.selectedSolicitudProyectoSocios, 'selectedSolicitudProyectoSocios');
    history.pushState(state.solicitudId, 'solicitudId');
    fixture = TestBed.createComponent(SolicitudProyectoSocioPeriodoPagoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
