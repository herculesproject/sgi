import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PeticionEvaluacionListadoInvComponent } from './peticion-evaluacion-listado-inv.component';
import { RouterTestingModule } from '@angular/router/testing';
import { MaterialDesignModule } from '@material/material-design.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';

describe('PeticionEvaluacionListadoInvComponent', () => {
  let component: PeticionEvaluacionListadoInvComponent;
  let fixture: ComponentFixture<PeticionEvaluacionListadoInvComponent>;

  beforeEach(async(() => {


    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        TestUtils.getIdiomas()
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: SgiAuthService, useValue: TestUtils.getSgiAuthServiceSpy() }
      ],
      declarations: [PeticionEvaluacionListadoInvComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PeticionEvaluacionListadoInvComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
