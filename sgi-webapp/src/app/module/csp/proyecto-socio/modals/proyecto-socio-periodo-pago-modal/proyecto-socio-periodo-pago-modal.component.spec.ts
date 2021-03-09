import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { IProyectoSocioPeriodoPago } from '@core/models/csp/proyecto-socio-periodo-pago';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { DateTime } from 'luxon';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ProyectoSocioPeriodoPagoModalComponent, ProyectoSocioPeriodoPagoModalData } from './proyecto-socio-periodo-pago-modal.component';

describe('ProyectoSocioPeriodoPagoModalComponent', () => {
  let component: ProyectoSocioPeriodoPagoModalComponent;
  let fixture: ComponentFixture<ProyectoSocioPeriodoPagoModalComponent>;

  const proyectoSocio: IProyectoSocio = {
    empresa: undefined,
    id: undefined,
    numInvestigadores: undefined,
    rolSocio: undefined,
    fechaFin: undefined,
    fechaInicio: undefined,
    importeConcedido: undefined,
    proyecto: undefined
  };

  const proyectoSocioPeriodoPago: IProyectoSocioPeriodoPago = {
    id: 1,
    importe: undefined,
    numPeriodo: undefined,
    fechaPago: undefined,
    fechaPrevistaPago: undefined,
    proyectoSocio
  };

  const newData: ProyectoSocioPeriodoPagoModalData = {
    isEdit: false,
    selectedFechaPrevistas: [],
    proyectoSocioPeriodoPago,
    fechaInicioProyectoSocio: DateTime.now(),
    fechaFinProyectoSocio: DateTime.now()
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ProyectoSocioPeriodoPagoModalComponent
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
    fixture = TestBed.createComponent(ProyectoSocioPeriodoPagoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
