import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { NGXLogger } from 'ngx-logger';

import {
  EvaluacionListadoAnteriorMemoriaComponent,
} from '../evaluacion-listado-anterior-memoria/evaluacion-listado-anterior-memoria.component';
import { EvaluacionDatosMemoriaComponent } from './evaluacion-datos-memoria.component';
import { EvaluacionEvaluadorEvaluarActionService } from '../../evaluacion-evaluador/evaluacion-evaluador.action.service';

describe('EvaluacionDatosMemoriaComponent', () => {
  let component: EvaluacionDatosMemoriaComponent;
  let fixture: ComponentFixture<EvaluacionDatosMemoriaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        EvaluacionDatosMemoriaComponent,
        EvaluacionListadoAnteriorMemoriaComponent
      ],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        ReactiveFormsModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        EvaluacionEvaluadorEvaluarActionService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EvaluacionDatosMemoriaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
