import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { StatusWrapper } from '@core/utils/status-wrapper';
import TestUtils from '@core/utils/test-utils';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { IProyectoPeriodoSeguimientoWithTituloProyecto } from '../../ejecucion-economica-formulario/seguimiento-justificacion-resumen/seguimiento-justificacion-resumen.fragment';

import { PresentacionDocumentacionModalComponent } from './presentacion-documentacion-modal.component';

describe('PresentacionDocumentacionModalComponent', () => {
  let component: PresentacionDocumentacionModalComponent;
  let fixture: ComponentFixture<PresentacionDocumentacionModalComponent>;

  const newData: StatusWrapper<IProyectoPeriodoSeguimientoWithTituloProyecto> =
    new StatusWrapper({} as IProyectoPeriodoSeguimientoWithTituloProyecto);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        PresentacionDocumentacionModalComponent
      ],
      imports: [
        MatDialogModule,
        FormsModule,
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        ReactiveFormsModule,
        SgiAuthModule,
        SharedModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: newData },
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PresentacionDocumentacionModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
