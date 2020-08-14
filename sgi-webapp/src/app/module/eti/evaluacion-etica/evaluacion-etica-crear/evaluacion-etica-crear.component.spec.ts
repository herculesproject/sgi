import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EvaluacionEticaCrearComponent } from './evaluacion-etica-crear.component';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { EvaluacionEticaDatosGeneralesComponent } from './evaluacion-etica-datos-generales/evaluacion-etica-datos-generales.component';
import { EvaluacionEticaEquipoInvestigadorComponent } from './evaluacion-etica-equipo-investigador/evaluacion-etica-equipo-investigador.component';
import { EvaluacionEticaAsignacionTareasComponent } from './evaluacion-etica-asignacion-tareas/evaluacion-etica-asignacion-tareas.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FooterGuardarComponent } from '@shared/footers/footer-guardar/footer-guardar.component';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { GenericTabLabelComponent } from '@shared/generic-tab-label/generic-tab-label.component';

describe('EvaluacionEticaCrearComponent', () => {
  let component: EvaluacionEticaCrearComponent;
  let fixture: ComponentFixture<EvaluacionEticaCrearComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        EvaluacionEticaCrearComponent,
        EvaluacionEticaDatosGeneralesComponent,
        EvaluacionEticaEquipoInvestigadorComponent,
        EvaluacionEticaAsignacionTareasComponent,
        FooterGuardarComponent,
        GenericTabLabelComponent
      ],
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
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EvaluacionEticaCrearComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
