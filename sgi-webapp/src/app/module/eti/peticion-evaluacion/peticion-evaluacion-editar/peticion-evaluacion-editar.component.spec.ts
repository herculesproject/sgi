import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PeticionEvaluacionEditarComponent } from './peticion-evaluacion-editar.component';
import { PeticionEvaluacionTareasListadoComponent } from '../peticion-evaluacion-formulario/peticion-evaluacion-tareas/peticion-evaluacion-tareas-listado/peticion-evaluacion-tareas-listado.component';
import { FooterGuardarComponent } from '@shared/footers/footer-guardar/footer-guardar.component';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NGXLogger } from 'ngx-logger';

describe('PeticionEvaluacionEditarComponent', () => {
  let component: PeticionEvaluacionEditarComponent;
  let fixture: ComponentFixture<PeticionEvaluacionEditarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        PeticionEvaluacionEditarComponent,
        PeticionEvaluacionTareasListadoComponent,
        FooterGuardarComponent
      ],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        FlexModule,
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([])
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PeticionEvaluacionEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
