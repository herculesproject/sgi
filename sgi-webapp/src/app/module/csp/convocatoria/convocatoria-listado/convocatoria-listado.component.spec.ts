import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConvocatoriaListadoComponent } from './convocatoria-listado.component';
import { FooterCrearComponent } from '@shared/footers/footer-crear/footer-crear.component';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FlexModule, FlexLayoutModule } from '@angular/flex-layout';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';

describe('ConvocatoriaListadoComponent', () => {
  let component: ConvocatoriaListadoComponent;
  let fixture: ComponentFixture<ConvocatoriaListadoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ConvocatoriaListadoComponent,
        FooterCrearComponent
      ],
      imports: [
        RouterTestingModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        TestUtils.getIdiomas(),
        FlexLayoutModule,
        FormsModule,
        ReactiveFormsModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
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

