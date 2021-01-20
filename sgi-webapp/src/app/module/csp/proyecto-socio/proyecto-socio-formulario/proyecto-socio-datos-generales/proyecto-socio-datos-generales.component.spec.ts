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
import { ProyectoSocioDatosGeneralesComponent } from './proyecto-socio-datos-generales.component';
import { ProyectoSocioActionService } from '../../proyecto-socio.action.service';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';

describe('ProyectoSocioDatosGeneralesComponent', () => {
  let component: ProyectoSocioDatosGeneralesComponent;
  let fixture: ComponentFixture<ProyectoSocioDatosGeneralesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ProyectoSocioDatosGeneralesComponent
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
        ProyectoSocioActionService,
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    const proyectoSocio = {} as IProyectoSocio;
    history.pushState({ proyectoSocio }, 'proyectoSocio');

    fixture = TestBed.createComponent(ProyectoSocioDatosGeneralesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
