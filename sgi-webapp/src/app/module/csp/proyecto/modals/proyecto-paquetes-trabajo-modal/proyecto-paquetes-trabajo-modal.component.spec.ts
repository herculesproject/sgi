import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { DialogComponent } from '@block/dialog/dialog.component';
import { HeaderComponent } from '@block/header/header.component';
import { IProyectoPaqueteTrabajo } from '@core/models/csp/proyecto-paquete-trabajo';
import TestUtils from '@core/utils/test-utils';
import { SgiAuthModule, SgiAuthService } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ProyectoPaquetesTrabajoModalComponent } from './proyecto-paquetes-trabajo-modal.component';

describe('ProyectoPaquetesTrabajoModalComponent', () => {
  let component: ProyectoPaquetesTrabajoModalComponent;
  let fixture: ComponentFixture<ProyectoPaquetesTrabajoModalComponent>;

  beforeEach(waitForAsync(() => {
    // Mock MAT_DIALOG
    const matDialogData = {} as IProyectoPaqueteTrabajo;

    TestBed.configureTestingModule({
      declarations: [
        ProyectoPaquetesTrabajoModalComponent,
        DialogComponent,
        HeaderComponent],
      imports: [
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
        { provide: MAT_DIALOG_DATA, useValue: matDialogData },
        SgiAuthService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProyectoPaquetesTrabajoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
