import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { Module } from '@core/module';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { SgiAuthModule, SgiAuthService } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { of } from 'rxjs';
import { PETICION_EVALUACION_UNIDADES_VINCULACION_ENABLED_KEY } from '../../peticion-evaluacion-unidades-vinculacion-enabled.resolver';
import { PeticionEvaluacionActionService } from '../../peticion-evaluacion.action.service';
import { PeticionEvaluacionUnidadesVinculacionComponent } from './peticion-evaluacion-unidades-vinculacion.component';

describe('PeticionEvaluacionUnidadesVinculacionComponent', () => {
  let component: PeticionEvaluacionUnidadesVinculacionComponent;
  let fixture: ComponentFixture<PeticionEvaluacionUnidadesVinculacionComponent>;
  const routeData: Data = {
    peticionEvaluacion: { id: 1, activo: true } as IPeticionEvaluacion,
    readonly: true,
    module: Module.ETI,
    [PETICION_EVALUACION_UNIDADES_VINCULACION_ENABLED_KEY]: true
  };
  const routeMock = {
    ...TestUtils.buildActivatedRouteMock('1', routeData),
    queryParams: of({})
  } as ActivatedRoute;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [PeticionEvaluacionUnidadesVinculacionComponent],
      imports: [
        RouterTestingModule,
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        FlexModule,
        FormsModule,
        ReactiveFormsModule,
        SgiAuthModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        SgiAuthService,
        PeticionEvaluacionActionService,
        { provide: ActivatedRoute, useValue: routeMock }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    spyOn(TestBed.inject(SgiAuthService), 'hasAnyAuthorityForAnyUO').and.returnValue(true);
    fixture = TestBed.createComponent(PeticionEvaluacionUnidadesVinculacionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
