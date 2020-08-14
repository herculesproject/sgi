import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EvaluacionEticaEquipoInvestigadorComponent } from './evaluacion-etica-equipo-investigador.component';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FlexModule } from '@angular/flex-layout';
import { MaterialDesignModule } from '@material/material-design.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';

describe('EvaluacionEticaEquipoInvestigadorComponent', () => {
  let component: EvaluacionEticaEquipoInvestigadorComponent;
  let fixture: ComponentFixture<EvaluacionEticaEquipoInvestigadorComponent>;
  const formBuilder: FormBuilder = new FormBuilder();

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EvaluacionEticaEquipoInvestigadorComponent],
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
    fixture = TestBed.createComponent(EvaluacionEticaEquipoInvestigadorComponent);
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
