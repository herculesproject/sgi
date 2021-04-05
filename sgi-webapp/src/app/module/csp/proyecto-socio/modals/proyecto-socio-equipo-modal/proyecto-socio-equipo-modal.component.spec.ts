import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IProyectoSocioEquipo } from '@core/models/csp/proyecto-socio-equipo';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { DateTime } from 'luxon';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ProyectoEquipoSocioModalData, ProyectoSocioEquipoModalComponent } from './proyecto-socio-equipo-modal.component';

describe('ProyectoSocioEquipoModalComponent', () => {
  let component: ProyectoSocioEquipoModalComponent;
  let fixture: ComponentFixture<ProyectoSocioEquipoModalComponent>;

  const proyectoSocioEquipo: IProyectoSocioEquipo = {
    fechaFin: undefined,
    fechaInicio: undefined,
    id: undefined,
    persona: undefined,
    proyectoSocioId: undefined,
    rolProyecto: undefined
  };

  const newData: ProyectoEquipoSocioModalData = {
    isEdit: false,
    proyectoSocioEquipo,
    selectedProyectoSocioEquipos: [],
    fechaFinProyectoSocio: DateTime.now(),
    fechaInicioProyectoSocio: DateTime.now()
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ProyectoSocioEquipoModalComponent
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
    fixture = TestBed.createComponent(ProyectoSocioEquipoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
