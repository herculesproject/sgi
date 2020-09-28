import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConvocatoriaListadoComponent } from './convocatoria-listado.component';
import { FooterCrearComponent } from '@shared/footers/footer-crear/footer-crear.component';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FlexModule } from '@angular/flex-layout';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NGXLogger } from 'ngx-logger';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';

describe('ConvocatoriaListadoComponent', () => {
  let component: ConvocatoriaListadoComponent;
  let fixture: ComponentFixture<ConvocatoriaListadoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        FooterCrearComponent,
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
        ConvocatoriaService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaListadoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

