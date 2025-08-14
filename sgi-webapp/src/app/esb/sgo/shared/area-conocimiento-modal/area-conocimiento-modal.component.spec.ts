import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { HeaderComponent } from '@block/header/header.component';
import TestUtils from '@core/utils/test-utils';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { AreaConocimientoDataModal, AreaConocimientoModalComponent } from './area-conocimiento-modal.component';

describe('AreaConocimientoModalComponent', () => {
  let component: AreaConocimientoModalComponent;
  let fixture: ComponentFixture<AreaConocimientoModalComponent>;

  beforeEach(waitForAsync(() => {
    // Mock MAT_DIALOG
    const matDialogData: AreaConocimientoDataModal = {
      selectedAreasConocimiento: [],
      multiSelect: true
    };

    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        RouterTestingModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        MatDialogModule,
        TestUtils.getIdiomas(),
        FormsModule,
        ReactiveFormsModule,
        SgiAuthModule
      ],
      providers: [
        {
          provide: MatDialogRef,
          useValue: TestUtils.buildDialogCommonMatDialogRef(),
        },
        { provide: MAT_DIALOG_DATA, useValue: matDialogData },
      ],
      declarations: [AreaConocimientoModalComponent, HeaderComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AreaConocimientoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
