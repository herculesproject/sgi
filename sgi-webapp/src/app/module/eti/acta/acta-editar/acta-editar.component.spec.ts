import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ActaEditarComponent } from './acta-editar.component';
import { ActaAsistentesListadoComponent } from '../acta-formulario/acta-asistentes/acta-asistentes-listado/acta-asistentes-listado.component';
import { ActaDatosGeneralesComponent } from '../acta-formulario/acta-datos-generales/acta-datos-generales.component';
import { ActaMemoriasComponent } from '../acta-formulario/acta-memorias/acta-memorias.component';
import { FooterGuardarComponent } from '@shared/footers/footer-guardar/footer-guardar.component';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NGXLogger } from 'ngx-logger';
import { ActaActionService } from '../acta.action.service';
import { ActivatedRoute } from '@angular/router';

describe('ActaEditarComponent', () => {
  let component: ActaEditarComponent;
  let fixture: ComponentFixture<ActaEditarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ActaEditarComponent,
        ActaAsistentesListadoComponent,
        ActaDatosGeneralesComponent,
        ActaMemoriasComponent,
        FooterGuardarComponent
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
        ActaActionService,
        ActivatedRoute
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActaEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
