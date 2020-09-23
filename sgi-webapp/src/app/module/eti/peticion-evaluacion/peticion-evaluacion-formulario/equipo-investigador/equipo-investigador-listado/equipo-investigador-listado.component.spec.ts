import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EquipoInvestigadorListadoComponent } from './equipo-investigador-listado.component';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { PeticionEvaluacionActionService } from '../../../peticion-evaluacion.action.service';

describe('EquipoInvestigadorListadoComponent', () => {
  let component: EquipoInvestigadorListadoComponent;
  let fixture: ComponentFixture<EquipoInvestigadorListadoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EquipoInvestigadorListadoComponent],
      imports: [
        RouterTestingModule,
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
        { provide: SgiAuthService, useValue: TestUtils.getSgiAuthServiceSpy() },
        PeticionEvaluacionActionService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EquipoInvestigadorListadoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
