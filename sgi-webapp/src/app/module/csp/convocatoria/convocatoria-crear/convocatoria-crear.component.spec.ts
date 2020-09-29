import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { NGXLogger } from 'ngx-logger';

import { ConvocatoriaCrearComponent } from './convocatoria-crear.component';
import { ConvocatoriaDatosGeneralesComponent } from '../convocatoria-formulario/convocatoria-datos-generales/convocatoria-datos-generales.component';
import { FooterGuardarComponent } from '@shared/footers/footer-guardar/footer-guardar.component';
import { FlexModule } from '@angular/flex-layout';
import { ConvocatoriaActionService } from '../convocatoria.action.service';

describe('ConvocatoriaCrearComponent', () => {
  let component: ConvocatoriaCrearComponent;
  let fixture: ComponentFixture<ConvocatoriaCrearComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ConvocatoriaCrearComponent,
        ConvocatoriaDatosGeneralesComponent,
        FooterGuardarComponent
      ],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        FlexModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([]),
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        ConvocatoriaActionService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaCrearComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
