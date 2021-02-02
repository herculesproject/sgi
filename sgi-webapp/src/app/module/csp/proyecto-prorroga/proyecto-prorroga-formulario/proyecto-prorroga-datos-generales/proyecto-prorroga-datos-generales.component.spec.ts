import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

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
import { ProyectoProrrogaDatosGeneralesComponent } from './proyecto-prorroga-datos-generales.component';
import { ProyectoProrrogaActionService } from '../../proyecto-prorroga.action.service';
import { IProyectoProrroga, TipoProrrogaEnum } from '@core/models/csp/proyecto-prorroga';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoProrrogaState } from '../../../proyecto/proyecto-formulario/proyecto-prorrogas/proyecto-prorrogas.component';

describe('ProyectoProrrogaDatosGeneralesComponent', () => {
  let component: ProyectoProrrogaDatosGeneralesComponent;
  let fixture: ComponentFixture<ProyectoProrrogaDatosGeneralesComponent>;

  const proyecto: IProyecto = {
    id: 1,
    fechaInicio: new Date(),
    fechaFin: new Date()
  } as IProyecto;

  const proyectoProrroga: IProyectoProrroga = {
    id: 1,
    proyecto,
    fechaConcesion: new Date(),
    tipoProrroga: TipoProrrogaEnum.TIEMPO,
    fechaFin: new Date(),
    numProrroga: 1
  } as IProyectoProrroga;

  const selectedProyectoProrrogas: IProyectoProrroga[] = [
    proyectoProrroga as IProyectoProrroga]

  const state: IProyectoProrrogaState = {
    proyecto,
    proyectoProrroga,
    selectedProyectoProrrogas,
    readonly: true
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ProyectoProrrogaDatosGeneralesComponent
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
        ProyectoProrrogaActionService,
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    history.pushState(state.proyectoProrroga, 'proyectoProrroga');
    history.pushState(state.selectedProyectoProrrogas, 'selectedProyectoProrrogas');
    history.pushState(state.proyecto, 'proyecto');
    history.pushState(state.readonly, 'readonly');

    fixture = TestBed.createComponent(ProyectoProrrogaDatosGeneralesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
