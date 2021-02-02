import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ActionFooterComponent } from '@shared/action-footer/action-footer.component';
import { FlexModule, FlexLayoutModule } from '@angular/flex-layout';

import { SharedModule } from '@shared/shared.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { SolicitudProyectoSocioEditarComponent } from './solicitud-proyecto-socio-editar.component';
import { SolicitudProyectoSocioActionService } from '../solicitud-proyecto-socio.action.service';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';

describe('SolicitudProyectoSocioEditarComponent', () => {
  let component: SolicitudProyectoSocioEditarComponent;
  let fixture: ComponentFixture<SolicitudProyectoSocioEditarComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        SolicitudProyectoSocioEditarComponent,
        ActionFooterComponent
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
        FlexLayoutModule,
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
    fixture = TestBed.createComponent(SolicitudProyectoSocioEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
