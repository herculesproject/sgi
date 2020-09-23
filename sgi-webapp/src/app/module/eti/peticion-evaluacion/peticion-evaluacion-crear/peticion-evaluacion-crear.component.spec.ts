import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PeticionEvaluacionCrearComponent } from './peticion-evaluacion-crear.component';
import { PeticionEvaluacionDatosGeneralesComponent } from '../peticion-evaluacion-formulario/peticion-evaluacion-datos-generales/peticion-evaluacion-datos-generales.component';
import { FooterGuardarComponent } from '@shared/footers/footer-guardar/footer-guardar.component';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NGXLogger } from 'ngx-logger';
import { SgiAuthService } from '@sgi/framework/auth';

describe('PeticionEvaluacionCrearComponent', () => {
  let component: PeticionEvaluacionCrearComponent;
  let fixture: ComponentFixture<PeticionEvaluacionCrearComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PeticionEvaluacionCrearComponent, PeticionEvaluacionDatosGeneralesComponent,
        FooterGuardarComponent],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        FlexModule,
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([]),
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PeticionEvaluacionCrearComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
