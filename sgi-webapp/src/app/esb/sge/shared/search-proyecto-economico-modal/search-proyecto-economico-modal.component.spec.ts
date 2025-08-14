import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { HeaderComponent } from '@block/header/header.component';
import TestUtils from '@core/utils/test-utils';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SearchProyectoEconomicoModalComponent } from './search-proyecto-economico-modal.component';

describe('SearchProyectoEconomicoModalComponent', () => {
  let component: SearchProyectoEconomicoModalComponent;
  let fixture: ComponentFixture<SearchProyectoEconomicoModalComponent>;

  beforeEach(waitForAsync(() => {
    // Mock MAT_DIALOG
    const matDialogData = {};

    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        RouterTestingModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        MatDialogModule,
        TestUtils.getIdiomas(),
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
      declarations: [SearchProyectoEconomicoModalComponent, HeaderComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SearchProyectoEconomicoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
