import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { IProyecto } from '@core/models/csp/proyecto';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { SgiAuthService } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { PROYECTO_AGRUPACION_GASTO_DATA_KEY } from '../../proyecto-agrupacion-gasto-data.resolver';
import { IProyectoAgrupacionGastoData, ProyectoAgrupacionGastoActionService } from '../../proyecto-agrupacion-gasto.action.service';

import { ProyectoAgrupacionGastoDatosGeneralesComponent } from './proyecto-agrupacion-gasto-datos-generales.component';

describe('ProyectoAgrupacionGastoDatosGeneralesComponent', () => {
  let component: ProyectoAgrupacionGastoDatosGeneralesComponent;
  let fixture: ComponentFixture<ProyectoAgrupacionGastoDatosGeneralesComponent>;
  const routeData: Data = {
    [PROYECTO_AGRUPACION_GASTO_DATA_KEY]: { proyecto: { id: 1 } as IProyecto } as IProyectoAgrupacionGastoData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ProyectoAgrupacionGastoDatosGeneralesComponent],
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
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        ProyectoAgrupacionGastoActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProyectoAgrupacionGastoDatosGeneralesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
