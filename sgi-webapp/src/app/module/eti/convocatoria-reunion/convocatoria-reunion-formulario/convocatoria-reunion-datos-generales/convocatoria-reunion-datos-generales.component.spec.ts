import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConvocatoriaReunionDatosGeneralesComponent } from './convocatoria-reunion-datos-generales.component';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';
import { ConvocatoriaReunionActionService } from '../../convocatoria-reunion.action.service';
import { ActivatedRoute } from '@angular/router';

describe('ConvocatoriaReunionDatosGeneralesComponent', () => {
  let component: ConvocatoriaReunionDatosGeneralesComponent;
  let fixture: ComponentFixture<ConvocatoriaReunionDatosGeneralesComponent>;

  const formBuilder: FormBuilder = new FormBuilder();

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConvocatoriaReunionDatosGeneralesComponent],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        FlexModule,
        ReactiveFormsModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: FormBuilder, useValue: formBuilder },
        ConvocatoriaReunionActionService,
        ActivatedRoute
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaReunionDatosGeneralesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
