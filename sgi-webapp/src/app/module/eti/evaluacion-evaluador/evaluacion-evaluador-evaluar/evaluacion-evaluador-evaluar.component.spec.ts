import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialDesignModule } from '@material/material-design.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import TestUtils from '@core/utils/test-utils';
import { RouterTestingModule } from '@angular/router/testing';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SharedModule } from '@shared/shared.module';
import { EvaluacionEvaluadorEvaluarComponent } from './evaluacion-evaluador-evaluar.component';

describe('EvaluacionEvaluadorEvaluarComponent', () => {
  let component: EvaluacionEvaluadorEvaluarComponent;
  let fixture: ComponentFixture<EvaluacionEvaluadorEvaluarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EvaluacionEvaluadorEvaluarComponent],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        SharedModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EvaluacionEvaluadorEvaluarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
