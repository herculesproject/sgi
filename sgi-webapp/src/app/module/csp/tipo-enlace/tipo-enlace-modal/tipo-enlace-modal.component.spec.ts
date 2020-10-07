import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { ITipoEnlace } from '@core/models/csp/tipo-enlace';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { NGXLogger } from 'ngx-logger';

import { TipoEnlaceModalComponent } from './tipo-enlace-modal.component';

describe('TipoEnlaceModalComponent', () => {
  let component: TipoEnlaceModalComponent;
  let fixture: ComponentFixture<TipoEnlaceModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        TipoEnlaceModalComponent
      ],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        FormsModule,
        ReactiveFormsModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: {} as ITipoEnlace },
        { provide: MAT_DIALOG_DATA, useValue: {} as ITipoEnlace },
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TipoEnlaceModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
