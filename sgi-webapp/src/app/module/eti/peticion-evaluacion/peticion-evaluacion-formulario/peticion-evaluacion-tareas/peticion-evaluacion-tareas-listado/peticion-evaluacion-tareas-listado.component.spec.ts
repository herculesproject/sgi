import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';

import { PeticionEvaluacionTareasListadoComponent } from './peticion-evaluacion-tareas-listado.component';
import { MaterialDesignModule } from '@material/material-design.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';

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
        { provide: FormBuilder, useValue: formBuilder }
      ],

    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PeticionEvaluacionTareasListadoComponent);
    component = fixture.componentInstance;

    // pass in the form dynamically
    component.formGroup = formBuilder.group({

    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
