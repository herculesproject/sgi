import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { FooterGuardarComponent } from '@shared/footers/footer-guardar/footer-guardar.component';
import { GenericTabLabelComponent } from '@shared/formularios-tabs/generic-tab-label/generic-tab-label.component';
import { NGXLogger } from 'ngx-logger';
import { PerfectScrollbarModule } from 'ngx-perfect-scrollbar';

import { ActaCrearAsistentesComponent } from './acta-crear-asistentes/acta-crear-asistentes.component';
import { ActaCrearDatosGeneralesComponent } from './acta-crear-datos-generales/acta-crear-datos-generales.component';
import { ActaCrearMemoriasComponent } from './acta-crear-memorias/acta-crear-memorias.component';
import { ActaCrearComponent } from './acta-crear.component';

describe('ActaCrearComponent', () => {
  let component: ActaCrearComponent;
  let fixture: ComponentFixture<ActaCrearComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ActaCrearComponent,
        ActaCrearAsistentesComponent,
        ActaCrearDatosGeneralesComponent,
        ActaCrearMemoriasComponent,
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
    fixture = TestBed.createComponent(ActaCrearComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
