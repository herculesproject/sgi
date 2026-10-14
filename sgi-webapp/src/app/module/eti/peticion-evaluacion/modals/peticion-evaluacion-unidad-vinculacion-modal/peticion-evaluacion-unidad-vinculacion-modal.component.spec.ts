import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgoSharedModule } from 'src/app/esb/sgo/shared/sgo-shared.module';
import {
  PeticionEvaluacionUnidadVinculacionModalComponent,
  PeticionEvaluacionUnidadVinculacionModalData
} from './peticion-evaluacion-unidad-vinculacion-modal.component';

describe('PeticionEvaluacionUnidadVinculacionModalComponent', () => {
  let component: PeticionEvaluacionUnidadVinculacionModalComponent;
  let fixture: ComponentFixture<PeticionEvaluacionUnidadVinculacionModalComponent>;

  const modalData: PeticionEvaluacionUnidadVinculacionModalData = {
    titleEntity: 'eti.peticion-evaluacion-unidad-vinculacion',
    selectedUnidadesVinculacionRefs: [],
    onlyActive: true
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [PeticionEvaluacionUnidadVinculacionModalComponent],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        FormsModule,
        ReactiveFormsModule,
        SharedModule,
        SgoSharedModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: modalData }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PeticionEvaluacionUnidadVinculacionModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
