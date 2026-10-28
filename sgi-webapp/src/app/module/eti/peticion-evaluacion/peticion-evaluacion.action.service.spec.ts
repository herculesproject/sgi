import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { FieldVisibility } from '@core/models/eti/configuracion';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { IPeticionEvaluacionAyudas } from '@core/models/eti/peticion-evaluacion-ayudas';
import { Module } from '@core/module';
import TestUtils from '@core/utils/test-utils';
import { SgiAuthService } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { FormlyModule } from '@ngx-formly/core';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { of } from 'rxjs';
import { PETICION_EVALUACION_DATA_KEY } from './peticion-evaluacion-data.resolver';
import { IPeticionEvaluacionData, PeticionEvaluacionActionService } from './peticion-evaluacion.action.service';

describe('PeticionEvaluacionActionService', () => {
  let service: PeticionEvaluacionActionService;

  beforeEach(() => {
    const routeData = {
      [PETICION_EVALUACION_DATA_KEY]: {
        peticionEvaluacion: { id: 1, activo: true } as IPeticionEvaluacion,
        isUnidadesVinculacionEnabled: true,
        introduccionVisibility: FieldVisibility.HIDDEN,
        ayudas: {} as IPeticionEvaluacionAyudas
      } as IPeticionEvaluacionData,
      readonly: true,
      module: Module.ETI
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
