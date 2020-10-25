import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { IApartado } from '@core/models/eti/apartado';
import { IComentario } from '@core/models/eti/comentario';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';

import { ComentarioEditarModalComponent } from './comentario-editar-modal.component';

describe('ComentarioActualizarModalComponent', () => {
  let component: ComentarioEditarModalComponent;
  let fixture: ComponentFixture<ComentarioEditarModalComponent>;

  const snapshotData = {
    comentario: {
      apartado: {
        id: 1
      } as IApartado,
    } as IComentario,
    idComite: 1,
    idTipoEvaluacion: 1
  };

  beforeEach(async(() => {

    TestBed.configureTestingModule({
      declarations: [ComentarioEditarModalComponent],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        ReactiveFormsModule,
        SgiAuthModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: snapshotData },
        { provide: MAT_DIALOG_DATA, useValue: snapshotData },
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ComentarioEditarModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
