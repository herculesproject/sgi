import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IEntidadConvocante } from '@core/models/csp/entidad-convocante';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { NGXLogger } from 'ngx-logger';

import { ConvocatoriaEntidadConvocanteaModalComponent } from './convocatoria-entidad-convocante-modal.component';

describe('ConvocatoriaEntidadConvocanteaModalComponent', () => {
  let component: ConvocatoriaEntidadConvocanteaModalComponent;
  let fixture: ComponentFixture<ConvocatoriaEntidadConvocanteaModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ConvocatoriaEntidadConvocanteaModalComponent
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
        { provide: MatDialogRef, useValue: {} as IEntidadConvocante },
        { provide: MAT_DIALOG_DATA, useValue: {} as IEntidadConvocante },
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaEntidadConvocanteaModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
