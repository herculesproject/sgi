import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CspSharedModule } from '../../../shared/csp-shared.module';
import { SolicitudProyectoUnidadVinculacionModalComponent, SolicitudProyectoUnidadVinculacionModalData } from './solicitud-proyecto-unidad-vinculacion-modal.component';

describe('SolicitudProyectoUnidadVinculacionModalComponent', () => {
  let component: SolicitudProyectoUnidadVinculacionModalComponent;
  let fixture: ComponentFixture<SolicitudProyectoUnidadVinculacionModalComponent>;

  const modalData: SolicitudProyectoUnidadVinculacionModalData = {
    titleEntity: 'csp.solicitud-proyecto-unidad-vinculacion',
    selectedUnidadesVinculacionRefs: []
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [SolicitudProyectoUnidadVinculacionModalComponent],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        FormsModule,
        ReactiveFormsModule,
        SharedModule,
        CspSharedModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: modalData }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudProyectoUnidadVinculacionModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
