import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { FooterGuardarComponent } from '@shared/footers/footer-guardar/footer-guardar.component';
import { GenericTabLabelComponent } from '@shared/generic-tab-label/generic-tab-label.component';
import { NGXLogger } from 'ngx-logger';

import {
  EvaluadorDatosGeneralesComponent,
} from '../evaluador-formulario/evaluador-datos-generales/evaluador-datos-generales.component';
import { EvaluadorEditarComponent } from './evaluador-editar.component';
import { SharedModule } from '@shared/shared.module';
import { BuscarPersonaComponent } from '@shared/buscar-persona/buscar-persona.component';

describe('EvaluadorEditarComponent', () => {
  let component: EvaluadorEditarComponent;
  let fixture: ComponentFixture<EvaluadorEditarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        EvaluadorEditarComponent,
        EvaluadorDatosGeneralesComponent,
        FooterGuardarComponent,
        GenericTabLabelComponent,
        BuscarPersonaComponent
      ],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        FlexModule,
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([]),
        SharedModule
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
