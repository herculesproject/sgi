import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { BuscarPersonaComponent } from '@shared/buscar-persona/buscar-persona.component';
import { SharedModule } from '@shared/shared.module';
import { NGXLogger } from 'ngx-logger';

import { ConfiguracionActionService } from '../../configuracion.action.service';
import { ConfiguracionDatosGeneralesComponent } from './configuracion-datos-generales.component';


describe('ConfiguracionDatosGenerealesComponent', () => {
  let component: ConfiguracionDatosGeneralesComponent;
  let fixture: ComponentFixture<ConfiguracionDatosGeneralesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ConfiguracionDatosGeneralesComponent,
        BuscarPersonaComponent
      ],
      imports: [
        RouterTestingModule,
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        FlexModule,
        SharedModule,
        FormsModule,
        ReactiveFormsModule,
        SgiAuthModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        ConfiguracionActionService,
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfiguracionDatosGeneralesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
