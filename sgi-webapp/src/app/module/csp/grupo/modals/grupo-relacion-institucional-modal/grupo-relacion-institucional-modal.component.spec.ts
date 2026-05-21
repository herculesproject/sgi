import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IGrupoRelacionInstitucional } from '@core/models/csp/grupo-relacion-institucional';
import TestUtils from '@core/utils/test-utils';
import { SgiAuthService } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../../../shared/csp-shared.module';
import { GrupoRelacionInstitucionalModalComponent, GrupoRelacionInstitucionalModalData } from './grupo-relacion-institucional-modal.component';

describe('GrupoRelacionInstitucionalModalComponent', () => {
  let component: GrupoRelacionInstitucionalModalComponent;
  let fixture: ComponentFixture<GrupoRelacionInstitucionalModalComponent>;

  const newData: GrupoRelacionInstitucionalModalData = {
    titleEntity: 'title',
    relacionInstitucional: {
    } as IGrupoRelacionInstitucional,
    relacionesExistentes: [],
    isEdit: true
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        GrupoRelacionInstitucionalModalComponent
      ],
      imports: [
        SharedModule,
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        ReactiveFormsModule,
        SgempSharedModule,
        SgpSharedModule,
        CspSharedModule
      ],
      providers: [
        SgiAuthService,
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: newData },
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GrupoRelacionInstitucionalModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
