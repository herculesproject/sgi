import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';

import { SeguimientoDatosMemoriaComponent } from './seguimiento-datos-memoria.component';
import { SeguimientoEvaluarActionService } from '../../seguimiento/seguimiento-evaluar.action.service';
import { SeguimientoListadoAnteriorMemoriaComponent } from '../seguimiento-listado-anterior-memoria/seguimiento-listado-anterior-memoria.component';
import { SeguimientoFormularioActionService } from '../seguimiento-formulario.action.service';

describe('SeguimientoDatosMemoriaComponent', () => {
  let component: SeguimientoDatosMemoriaComponent;
  let fixture: ComponentFixture<SeguimientoDatosMemoriaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        SeguimientoDatosMemoriaComponent,
        SeguimientoListadoAnteriorMemoriaComponent
      ],
      imports: [
        FormsModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        FormsModule,
        ReactiveFormsModule,
        SgiAuthModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: SeguimientoFormularioActionService, useClass: SeguimientoEvaluarActionService },
        SgiAuthService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SeguimientoDatosMemoriaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
