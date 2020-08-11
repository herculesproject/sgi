import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { NGXLogger } from 'ngx-logger';

import { ActaDatosGeneralesComponent } from './acta-datos-generales.component';


describe('ActaCrearDatosGenerealesComponent', () => {
  let component: ActaDatosGeneralesComponent;
  let fixture: ComponentFixture<ActaDatosGeneralesComponent>;
  const formBuilder: FormBuilder = new FormBuilder();

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ActaDatosGeneralesComponent],
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
    fixture = TestBed.createComponent(ActaDatosGeneralesComponent);
    component = fixture.componentInstance;
    // pass in the form dynamically
    component.formGroup = formBuilder.group({
      convocatoriaReunion: null,
      horaInicio: null,
      minutoInicio: null,
      horaFin: null,
      minutoFin: null,
      resumen: null,
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
