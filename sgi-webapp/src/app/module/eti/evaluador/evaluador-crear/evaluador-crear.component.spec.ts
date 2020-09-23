import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule } from '@sgi/framework/auth';
import { FooterGuardarComponent } from '@shared/footers/footer-guardar/footer-guardar.component';
import { SharedModule } from '@shared/shared.module';
import { NGXLogger } from 'ngx-logger';

import {
  EvaluadorDatosGeneralesComponent,
} from '../evaluador-formulario/evaluador-datos-generales/evaluador-datos-generales.component';
import { EvaluadorCrearComponent } from './evaluador-crear.component';

describe('EvaluadorCrearComponent', () => {
  let component: EvaluadorCrearComponent;
  let fixture: ComponentFixture<EvaluadorCrearComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        EvaluadorCrearComponent,
        EvaluadorDatosGeneralesComponent,
        FooterGuardarComponent
      ],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        ReactiveFormsModule,
        SharedModule,
        SgiAuthModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EvaluadorCrearComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
