import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';

import { PeticionEvaluacionTareasListadoComponent } from './peticion-evaluacion-tareas-listado.component';
import { MaterialDesignModule } from '@material/material-design.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { PeticionEvaluacionActionService } from '../../../peticion-evaluacion.action.service';
import { ActivatedRoute } from '@angular/router';

describe('PeticionEvaluacionTareasListadoComponent', () => {
  let component: PeticionEvaluacionTareasListadoComponent;
  let fixture: ComponentFixture<PeticionEvaluacionTareasListadoComponent>;
  const formBuilder: FormBuilder = new FormBuilder();

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        PeticionEvaluacionTareasListadoComponent,
      ],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        FlexModule,
        ReactiveFormsModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: FormBuilder, useValue: formBuilder },
        PeticionEvaluacionActionService,
        ActivatedRoute
      ],

    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PeticionEvaluacionTareasListadoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
