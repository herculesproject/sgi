import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { Module } from '@core/module';
import TestUtils from '@core/utils/test-utils';
import { SgiAuthService } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { FormlyModule } from '@ngx-formly/core';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { of } from 'rxjs';
import { PETICION_EVALUACION_UNIDADES_VINCULACION_ENABLED_KEY } from './peticion-evaluacion-unidades-vinculacion-enabled.resolver';
import { PeticionEvaluacionActionService } from './peticion-evaluacion.action.service';

describe('PeticionEvaluacionActionService', () => {
  let service: PeticionEvaluacionActionService;

  beforeEach(() => {
    const routeData = {
      peticionEvaluacion: { id: 1, activo: true } as IPeticionEvaluacion,
      readonly: true,
      module: Module.ETI,
      [PETICION_EVALUACION_UNIDADES_VINCULACION_ENABLED_KEY]: true
    };

    TestBed.configureTestingModule({
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        RouterTestingModule.withRoutes([]),
        FormlyModule.forRoot()
      ],
      providers: [
        PeticionEvaluacionActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: { snapshot: { data: routeData }, queryParams: of({}) } }
      ]
    });

    service = TestBed.inject(PeticionEvaluacionActionService);
  });

  it('should create', () => {
    expect(service).toBeTruthy();
  });
});
