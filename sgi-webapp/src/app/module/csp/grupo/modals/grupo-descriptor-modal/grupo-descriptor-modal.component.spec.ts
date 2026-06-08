import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { SgiAuthService } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CspSharedModule } from '../../../shared/csp-shared.module';
import { GrupoUnidadVinculacionModalData } from '../grupo-unidad-vinculacion-modal/grupo-unidad-vinculacion-modal.component';
import { GrupoDescriptorModalComponent } from './grupo-descriptor-modal.component';

describe('GrupoDescriptorModalComponent', () => {
  let component: GrupoDescriptorModalComponent;
  let fixture: ComponentFixture<GrupoDescriptorModalComponent>;

  const modalData: GrupoUnidadVinculacionModalData = {
    titleEntity: 'csp.grupo-unidad-vinculacion',
    selectedUnidadesVinculacionRefs: []
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [GrupoDescriptorModalComponent],
      imports: [
        SharedModule,
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
        SgiAuthService,
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: modalData }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GrupoDescriptorModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
