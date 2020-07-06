import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AgrupacionServicioCrearComponent} from './agrupacion-servicio-crear.component';
import {NGXLogger} from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import {SnackBarService} from '@core/services/snack-bar.service';
import {RouterTestingModule} from '@angular/router/testing';
import {MaterialDesignModule} from '@material/material-design.module';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {FormBuilder} from '@angular/forms';
import {AgrupacionServicioDatosGeneralesComponent} from '../agrupacion-servicio-formulario/agrupacion-servicio-datos-generales/agrupacion-servicio-datos-generales.component';

describe('AgrupacionServicioCrearComponent', () => {
  let component: AgrupacionServicioCrearComponent;
  let fixture: ComponentFixture<AgrupacionServicioCrearComponent>;

  beforeEach(async(() => {
    // Mock formBuilder
    const formBuilderSpy: jasmine.SpyObj<FormBuilder> = jasmine.createSpyObj(FormBuilder.name,
      TestUtils.getOwnMethodNames(FormBuilder.prototype));

    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        TestUtils.getIdiomas(),
      ],
      providers: [
        {provide: NGXLogger, useValue: TestUtils.getLoggerSpy()},
        {provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy()},
        {provide: FormBuilder, useValue: formBuilderSpy}
      ],
      declarations: [
        AgrupacionServicioCrearComponent,
        AgrupacionServicioDatosGeneralesComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AgrupacionServicioCrearComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
