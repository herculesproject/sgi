import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { NGXLogger } from 'ngx-logger';

import {
  DocumentacionMemoriaListadoMemoriaComponent,
} from '../../documentacion-memoria/documentacion-memoria-listado-memoria/documentacion-memoria-listado-memoria.component';
import {
  EvaluacionComentariosComponent,
} from '../evaluacion-formulario/evaluacion-comentarios/evaluacion-comentarios.component';
import {
  EvaluacionDatosMemoriaComponent,
} from '../evaluacion-formulario/evaluacion-datos-memoria/evaluacion-datos-memoria.component';
import {
  EvaluacionDocumentacionComponent,
} from '../evaluacion-formulario/evaluacion-documentacion/evaluacion-documentacion.component';
import {
  EvaluacionListadoAnteriorMemoriaComponent,
} from '../evaluacion-formulario/evaluacion-listado-anterior-memoria/evaluacion-listado-anterior-memoria.component';
import { EvaluacionEvaluarComponent } from './evaluacion-evaluar.component';

describe('EvaluacionEvaluarComponent', () => {
  let component: EvaluacionEvaluarComponent;
  let fixture: ComponentFixture<EvaluacionEvaluarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        EvaluacionEvaluarComponent,
        EvaluacionComentariosComponent,
        EvaluacionDatosMemoriaComponent,
        EvaluacionDocumentacionComponent,
        DocumentacionMemoriaListadoMemoriaComponent,
        EvaluacionListadoAnteriorMemoriaComponent
      ],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        SharedModule,
        ReactiveFormsModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EvaluacionEvaluarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
