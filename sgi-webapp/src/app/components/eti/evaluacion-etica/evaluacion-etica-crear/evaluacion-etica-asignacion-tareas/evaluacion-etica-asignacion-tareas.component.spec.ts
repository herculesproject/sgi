import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { NGXLogger } from 'ngx-logger';

import { EvaluacionEticaAsignacionTareasComponent } from './evaluacion-etica-asignacion-tareas.component';

describe('EvaluacionEticaEquipoAsignacionTareasComponent', () => {
  let component: EvaluacionEticaAsignacionTareasComponent;
  let fixture: ComponentFixture<EvaluacionEticaAsignacionTareasComponent>;
  const formBuilder: FormBuilder = new FormBuilder();

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EvaluacionEticaAsignacionTareasComponent],
      imports: [
        TestUtils.getIdiomas(),
        HttpClientTestingModule,
        FlexModule,
        MaterialDesignModule,
        BrowserAnimationsModule,
        ReactiveFormsModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: FormBuilder, useValue: formBuilder }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EvaluacionEticaAsignacionTareasComponent);
    component = fixture.componentInstance;

    // pass in the form dynamically
    component.formGroup = formBuilder.group({
      titulo: null
    });

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
