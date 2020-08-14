import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConvocatoriaReunionDatosGeneralesComponent } from './convocatoria-reunion-datos-generales.component';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';

describe('ConvocatoriaReunionDatosGeneralesComponent', () => {
  let component: ConvocatoriaReunionDatosGeneralesComponent;
  let fixture: ComponentFixture<ConvocatoriaReunionDatosGeneralesComponent>;

  const formBuilder: FormBuilder = new FormBuilder();

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConvocatoriaReunionDatosGeneralesComponent],
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
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: FormBuilder, useValue: formBuilder }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaReunionDatosGeneralesComponent);
    component = fixture.componentInstance;

    // pass in the form dynamically
    component.formGroup = formBuilder.group({
      comite: null,
      fechaEvaluacion: null,
      fechaLimite: null,
      tipoConvocatoriaReunion: null,
      horaInicio: null,
      minutoInicio: null,
      lugar: null,
      ordenDia: null,
      convocantes: null,
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
