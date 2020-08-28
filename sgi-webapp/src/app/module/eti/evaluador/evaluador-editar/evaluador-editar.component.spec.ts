import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EvaluadorEditarComponent } from './evaluador-editar.component';
import { EvaluadorDatosGeneralesComponent } from '../evaluador-formulario/evaluador-datos-generales/evaluador-datos-generales.component';
import { FooterGuardarComponent } from '@shared/footers/footer-guardar/footer-guardar.component';
import { GenericTabLabelComponent } from '@shared/formularios-tabs/generic-tab-label/generic-tab-label.component';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { PerfectScrollbarModule } from 'ngx-perfect-scrollbar';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NGXLogger } from 'ngx-logger';

describe('EvaluadorEditarComponent', () => {
  let component: EvaluadorEditarComponent;
  let fixture: ComponentFixture<EvaluadorEditarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        EvaluadorEditarComponent,
        EvaluadorDatosGeneralesComponent,
        FooterGuardarComponent,
        GenericTabLabelComponent
      ],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        PerfectScrollbarModule,
        HttpClientTestingModule,
        FlexModule,
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([])
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EvaluadorEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
