import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoProrroga, Tipo } from '@core/models/csp/proyecto-prorroga';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { ActionFooterComponent } from '@shared/action-footer/action-footer.component';
import { SharedModule } from '@shared/shared.module';
import { DateTime } from 'luxon';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { IProyectoProrrogaState } from '../../proyecto/proyecto-formulario/proyecto-prorrogas/proyecto-prorrogas.component';
import { ProyectoProrrogaActionService } from '../proyecto-prorroga.action.service';
import { ProyectoProrrogaEditarComponent } from './proyecto-prorroga-editar.component';

describe('ProyectoProrrogaEditarComponent', () => {
  let component: ProyectoProrrogaEditarComponent;
  let fixture: ComponentFixture<ProyectoProrrogaEditarComponent>;

  const proyecto: IProyecto = {
    id: 1,
    fechaInicio: DateTime.now(),
    fechaFin: DateTime.now()
  } as IProyecto;

  const proyectoProrroga: IProyectoProrroga = {
    id: 1,
    proyecto,
    fechaConcesion: DateTime.now(),
    tipo: Tipo.TIEMPO,
    fechaFin: DateTime.now(),
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
        ProyectoProrrogaEditarComponent,
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
        ProyectoProrrogaActionService,
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    spyOnProperty(history, 'state', 'get').and.returnValue(state);

    fixture = TestBed.createComponent(ProyectoProrrogaEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
