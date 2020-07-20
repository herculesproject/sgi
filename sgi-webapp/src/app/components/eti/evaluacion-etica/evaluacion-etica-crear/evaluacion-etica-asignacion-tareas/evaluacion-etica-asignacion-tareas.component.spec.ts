import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {EvaluacionEticaAsignacionTareasComponent} from './evaluacion-etica-asignacion-tareas.component';
import {NGXLogger} from 'ngx-logger';
import TestUtils from '../../../../../core/utils/test-utils';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {FlexModule} from '@angular/flex-layout';
import {MaterialDesignModule} from '@material/material-design.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ReactiveFormsModule} from '@angular/forms';

describe('EvaluacionEticaEquipoAsignacionTareasComponent', () => {
  let component: EvaluacionEticaAsignacionTareasComponent;
  let fixture: ComponentFixture<EvaluacionEticaAsignacionTareasComponent>;

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
        {provide: NGXLogger, useValue: TestUtils.getLoggerSpy()},
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EvaluacionEticaAsignacionTareasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
