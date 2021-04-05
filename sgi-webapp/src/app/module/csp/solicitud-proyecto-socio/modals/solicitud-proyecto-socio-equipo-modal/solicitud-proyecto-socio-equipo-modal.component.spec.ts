import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { ISolicitudProyectoSocioEquipo } from '@core/models/csp/solicitud-proyecto-socio-equipo';
import { IPersona } from '@core/models/sgp/persona';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudProyectoSocioEquipoModalComponent, SolicitudProyectoSocioEquipoModalData } from './solicitud-proyecto-socio-equipo-modal.component';

describe('SolicitudProyectoSocioEquipoSocioModalComponent', () => {
  let component: SolicitudProyectoSocioEquipoModalComponent;
  let fixture: ComponentFixture<SolicitudProyectoSocioEquipoModalComponent>;

  const rolProyecto: IRolProyecto = {
    abreviatura: undefined,
    activo: undefined,
    colectivoRef: undefined,
    descripcion: undefined,
    equipo: undefined,
    id: undefined,
    nombre: undefined,
    responsableEconomico: undefined,
    rolPrincipal: undefined
  };

  const persona: IPersona = {
    identificadorLetra: undefined,
    identificadorNumero: undefined,
    nivelAcademico: undefined,
    nombre: undefined,
    personaRef: undefined,
    primerApellido: undefined,
    segundoApellido: undefined,
    vinculacion: undefined
  };

  const solicitudProyectoSocioEquipo: ISolicitudProyectoSocioEquipo = {
    id: 1,
    mesFin: 2,
    mesInicio: 1,
    persona,
    rolProyecto,
    solicitudProyectoSocioId: undefined,
  };

  const newData: SolicitudProyectoSocioEquipoModalData = {
    isEdit: false,
    duracion: 0,
    selectedProyectoSocioEquipo: [],
    solicitudProyectoSocioEquipo,
    mesInicioSolicitudProyectoSocio: 1,
    mesFinSolicitudProyectoSocio: 2,
    readonly: false
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        SolicitudProyectoSocioEquipoModalComponent
      ],
      imports: [
        SharedModule,
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        ReactiveFormsModule,
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: newData },
        { provide: MAT_DIALOG_DATA, useValue: newData },
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudProyectoSocioEquipoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
