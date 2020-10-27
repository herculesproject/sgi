import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IComentario } from '@core/models/eti/comentario';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';

import { ComentarioCrearModalComponent } from './comentario-crear-modal.component';
import { IApartado } from '@core/models/eti/apartado';
import { TipoEvaluacion } from '@core/models/eti/tipo-evaluacion';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IComite } from '@core/models/eti/comite';

describe('ComentarioCrearModalComponent', () => {
  let component: ComentarioCrearModalComponent;
  let fixture: ComponentFixture<ComentarioCrearModalComponent>;

  beforeEach(async(() => {

    const snapshotData = {
      tipoEvaluacion: {
        id: 1
      } as TipoEvaluacion,
      memoria: {
        id: 1,
        comite: {
          id: 1
        } as IComite,
      }
    };

    TestBed.configureTestingModule({
      declarations: [ComentarioCrearModalComponent],
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
        { provide: MatDialogRef, useValue: snapshotData },
        { provide: MAT_DIALOG_DATA, useValue: snapshotData },
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ComentarioCrearModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
