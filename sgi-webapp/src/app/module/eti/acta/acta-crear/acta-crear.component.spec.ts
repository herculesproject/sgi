import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { FooterGuardarComponent } from '@shared/footers/footer-guardar/footer-guardar.component';
import { NGXLogger } from 'ngx-logger';

import { ActaCrearComponent } from './acta-crear.component';
import { ActaAsistentesListadoComponent } from '../acta-formulario/acta-asistentes/acta-asistentes-listado/acta-asistentes-listado.component';
import { ActaDatosGeneralesComponent } from '../acta-formulario/acta-datos-generales/acta-datos-generales.component';
import { ActaMemoriasComponent } from '../acta-formulario/acta-memorias/acta-memorias.component';
import { ActaActionService } from '../acta.action.service';

describe('ActaCrearComponent', () => {
  let component: ActaCrearComponent;
  let fixture: ComponentFixture<ActaCrearComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ActaCrearComponent,
        ActaAsistentesListadoComponent,
        ActaDatosGeneralesComponent,
        ActaMemoriasComponent,
        FooterGuardarComponent,
      ],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        FlexModule,
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([])
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        ActaActionService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActaCrearComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
