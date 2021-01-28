import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
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
import { ProyectoPeriodoSeguimientoCrearComponent } from './proyecto-periodo-seguimiento-crear.component';
import { ProyectoPeriodoSeguimientoActionService } from '../proyecto-periodo-seguimiento.action.service';
import { IProyectoPeriodoSeguimiento } from '@core/models/csp/proyecto-periodo-seguimiento';


describe('ProyectoPeriodoSeguimientoCrearComponent', () => {
  let component: ProyectoPeriodoSeguimientoCrearComponent;
  let fixture: ComponentFixture<ProyectoPeriodoSeguimientoCrearComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ProyectoPeriodoSeguimientoCrearComponent,
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
        ProyectoPeriodoSeguimientoActionService,
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    const proyectoPeriodoSeguimiento = {} as IProyectoPeriodoSeguimiento;
    history.pushState({ proyectoPeriodoSeguimiento }, 'proyectoPeriodoSeguimiento');

    fixture = TestBed.createComponent(ProyectoPeriodoSeguimientoCrearComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
