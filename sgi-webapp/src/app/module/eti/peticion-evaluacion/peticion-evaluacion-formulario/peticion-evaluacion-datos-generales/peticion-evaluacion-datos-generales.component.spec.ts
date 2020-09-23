import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PeticionEvaluacionDatosGeneralesComponent } from './peticion-evaluacion-datos-generales.component';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { HttpClient, HttpHandler } from '@angular/common/http';
import { PeticionEvaluacionActionService } from '../../peticion-evaluacion.action.service';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

describe('PeticionEvaluacionDatosGeneralesComponent', () => {
  let component: PeticionEvaluacionDatosGeneralesComponent;
  let fixture: ComponentFixture<PeticionEvaluacionDatosGeneralesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PeticionEvaluacionDatosGeneralesComponent],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        PeticionEvaluacionActionService,
        HttpClient,
        HttpHandler,
        FormBuilder,
        ActivatedRoute
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PeticionEvaluacionDatosGeneralesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
