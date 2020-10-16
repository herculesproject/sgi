import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SnackBarService } from '@core/services/snack-bar.service';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { MemoriaEvaluacionesComponent } from './memoria-evaluaciones.component';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MemoriaActionService } from '../../memoria.action.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { IMemoria } from '@core/models/eti/memoria';


describe('MemoriaEvaluacionesComponent', () => {
  let component: MemoriaEvaluacionesComponent;
  let fixture: ComponentFixture<MemoriaEvaluacionesComponent>;
  const snapshotData = {
    memoria: {
      peticionEvaluacion: {
        id: 1
      } as IPeticionEvaluacion
    } as IMemoria
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        MemoriaEvaluacionesComponent
      ],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        FormsModule,
        ReactiveFormsModule,
        SgiAuthModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: {} },
        { provide: ActivatedRoute, useValue: { snapshot: { data: snapshotData } } },
        MemoriaActionService,
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MemoriaEvaluacionesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});