import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AgrupacionServicioListadoComponent} from './agrupacion-servicio-listado.component';
import {NGXLogger} from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import {RouterTestingModule} from '@angular/router/testing';
import {MaterialDesignModule} from '@material/material-design.module';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

describe('AgrupacionServicioListadoComponent', () => {
  let component: AgrupacionServicioListadoComponent;
  let fixture: ComponentFixture<AgrupacionServicioListadoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        TestUtils.getIdiomas()
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() }
      ],
      declarations: [AgrupacionServicioListadoComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AgrupacionServicioListadoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
