import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EvaluacionEticaListadoComponent } from './evaluacion-etica-listado.component';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { RouterTestingModule } from '@angular/router/testing';
import { FooterGuardarComponent } from '@shared/footers/footer-guardar/footer-guardar.component';

describe('EvaluacionEticaListadoComponent', () => {
  let component: EvaluacionEticaListadoComponent;
  let fixture: ComponentFixture<EvaluacionEticaListadoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        EvaluacionEticaListadoComponent,
        FooterGuardarComponent,
      ],
      imports: [
        MaterialDesignModule,
        RouterTestingModule,
        TestUtils.getIdiomas()
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EvaluacionEticaListadoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
