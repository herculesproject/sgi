import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SolicitudProyectoSocioDatosGeneralesComponent } from './solicitud-proyecto-socio-datos-generales.component';
import { SolicitudProyectoSocioActionService } from '../../solicitud-proyecto-socio.action.service';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';

describe('SolicitudProyectoSocioDatosGeneralesComponent', () => {
  let component: SolicitudProyectoSocioDatosGeneralesComponent;
  let fixture: ComponentFixture<SolicitudProyectoSocioDatosGeneralesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        SolicitudProyectoSocioDatosGeneralesComponent
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
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
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
    history.pushState(solicitudProyectoSocio, 'solicitudProyectoSocio');
    fixture = TestBed.createComponent(SolicitudProyectoSocioDatosGeneralesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
